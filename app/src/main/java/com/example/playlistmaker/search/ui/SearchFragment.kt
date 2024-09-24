package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    companion object {
        private const val TEXT_EMPTY = ""
        private const val TRACK_KEY = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter {
            viewModel.onClickEvent(it)
            startPlayerActivity(it)
        }
    }

    private val textWatcher: TextWatcher by lazy {
        createSearchFieldWatcher()
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        observeViewModel()
    }

    override fun onDestroyView() {
        textWatcher.let { binding.searchField.removeTextChangedListener(it) }
        super.onDestroyView()
    }

    private fun initializeUI() {
        setupListeners()
        setupSearchField()
    }

    private fun setupListeners() {

        binding.updateButton.setOnClickListener {
            viewModel.repeatSearchQuery()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.clearButton.setOnClickListener {
            binding.searchField.setText(TEXT_EMPTY)
            viewModel.onTextChanged(TEXT_EMPTY)
            hideKeyboard(it)
        }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(textWatcher)

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.repeatSearchQuery()
            }
            false
        }

        binding.searchField.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus && binding.searchField.text.isEmpty()) {
                viewModel.onTextChanged(TEXT_EMPTY)
            }
            viewModel.onTextChanged(binding.searchField.text.toString())
        }
    }

    private fun createSearchFieldWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    viewModel.onTextChanged(s.toString())
                }
                binding.clearButton.isVisible = clearButtonVisibility(s)

                if (binding.searchField.hasFocus() && s?.isEmpty() == true) {
                    viewModel.onTextChanged(TEXT_EMPTY)
                }

                viewModel.onTextChanged(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_KEY, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun observeViewModel() {
        viewModel.searchScreenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager? =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager //?????
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.NoInternetError -> showNoInternetMessage()
            is SearchScreenState.NothingFound -> showNotFoundMessage()
            is SearchScreenState.SearchQueryResults -> showSearchQueryResults(state.foundTracks)
            is SearchScreenState.SearchHistory -> showSearchHistory(state.searchedTracks)
            is SearchScreenState.EmptyScreen -> showEmptyScreen()
        }
    }

    private fun showLoading() {
        binding.placeholderNotFound.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.rvSearch.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showNoInternetMessage() {
        binding.rvSearch.isVisible = false
        binding.progressBar.isVisible = false
        binding.placeholderNotFound.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.placeholderNoInternet.isVisible = true
    }

    private fun showNotFoundMessage() {
        binding.placeholderNoInternet.isVisible = false
        binding.rvSearch.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.placeholderNotFound.isVisible = true
    }

    private fun showSearchQueryResults(foundTracks: List<Track>) {
        binding.placeholderNoInternet.isVisible = false
        binding.placeholderNotFound.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.rvSearch.isVisible = true
        searchAdapter.setTracks(foundTracks)
        binding.rvSearch.adapter = searchAdapter
    }

    private fun showSearchHistory(searchedTracks: List<Track>) {
        binding.placeholderNoInternet.isVisible = false
        binding.placeholderNotFound.isVisible = false
        binding.progressBar.isVisible = false
        binding.rvSearch.isVisible = false
        binding.searchHistoryViewGroup.isVisible = true
        searchAdapter.setTracks(searchedTracks)
        binding.rvSearchHistory.adapter = searchAdapter
    }

    private fun showEmptyScreen() {
        binding.placeholderNotFound.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.rvSearch.isVisible = false
        binding.progressBar.isVisible = false
    }
}