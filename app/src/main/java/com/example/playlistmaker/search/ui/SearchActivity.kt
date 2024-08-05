package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModelFactory
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TEXT_EMPTY = ""
        private const val TRACK_KEY = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchViewModelFactory())[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        observeViewModel()
    }

    private fun initializeUI() {
        setupListeners()
        setupSearchField()
        createSearchAdapter()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.updateButton.setOnClickListener {
            viewModel.repeatSearchQuery()
        }

        binding.clearHistoryButton.setOnClickListener{
            viewModel.clearSearchHistory()
        }

        binding.clearButton.setOnClickListener {
            binding.searchField.setText(TEXT_EMPTY)
            viewModel.onTextChanged(TEXT_EMPTY)
            hideKeyboard(it)
        }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(createSearchFieldWatcher())

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.repeatSearchQuery()
            }
            false
        }

        binding.searchField.setOnFocusChangeListener {_, hasFocus ->

            if (hasFocus && binding.searchField.text.isEmpty()) {
                viewModel.onTextChanged(TEXT_EMPTY)
            }
            viewModel.onTextChanged(binding.searchField.text.toString())
        }
    }

    private fun createSearchFieldWatcher() : TextWatcher {
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

    private fun createSearchAdapter() {
        searchAdapter = SearchAdapter {
            viewModel.onClickEvent(it)
            startPlayerActivity(it)
        }
    }

    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_KEY, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun observeViewModel() {
        viewModel.searchScreenState.observe(this) { state ->
            render(state)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed( { isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun render(state: SearchScreenState){
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

    private fun showSearchQueryResults(foundTracks: MutableList<Track>) {
        binding.placeholderNoInternet.isVisible = false
        binding.placeholderNotFound.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchHistoryViewGroup.isVisible = false
        binding.rvSearch.isVisible = true
        searchAdapter.setTracks(foundTracks)
        binding.rvSearch.adapter = searchAdapter
    }

    private fun showSearchHistory(searchedTracks: MutableList<Track>) {
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