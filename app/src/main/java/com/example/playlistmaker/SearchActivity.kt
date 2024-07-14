package com.example.playlistmaker

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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val TEXT_INPUT = "TEXT_INPUT"
        const val TEXT_EMPTY = ""
        const val TRACK_KEY = "track"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var notFoundPlaceholder: LinearLayout
    private lateinit var noInternetConnectionPlaceholder: LinearLayout
    private lateinit var rvSearch: RecyclerView
    private lateinit var updateButton: MaterialButton
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var clearButton: ImageView
    private lateinit var searchField: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var rvSearchHistory: RecyclerView
    private lateinit var searchHistoryViewGroup: LinearLayout

    private var savedInput: String? = null //?

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchViewModelFactory())[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //viewModel

        val backButton = findViewById<MaterialButton>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        rvSearch = findViewById(R.id.rv_search)
        createSearchAdapter()
        observeScreenState() // После инициализации адаптера

        notFoundPlaceholder = findViewById(R.id.placeholder_not_found)
        noInternetConnectionPlaceholder = findViewById(R.id.placeholder_no_internet)
        updateButton = findViewById(R.id.update_btn)
        updateButton.setOnClickListener {
            searchQuery(savedInput)
            // viewModel.searchQuery(savedInput)
        }

        // История поиска
        rvSearchHistory = findViewById(R.id.rv_search_history)
        searchHistoryViewGroup = findViewById(R.id.search_history_viewgroup)
        val clearHistoryButton = findViewById<MaterialButton>(R.id.clear_history_btn)
        clearHistoryButton.setOnClickListener{
            searchHistoryInteractor.clear()
            searchHistoryViewGroup.isVisible = false
        }


        searchField = findViewById<EditText>(R.id.search_bar)
        savedInstanceState ?: searchField.setText(savedInput)

        clearButton = findViewById<ImageView>(R.id.clear_icon)
        clearButton.setOnClickListener {
            searchField.setText(TEXT_EMPTY)
            rvSearch.isVisible = false
            hideKeyboard(it)
            searchDebounce()
        }

        val searchFieldWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    savedInput = s.toString()
                }
                clearButton.isVisible = clearButtonVisibility(s)
                searchDebounce()

                if (searchField.hasFocus() && s?.isEmpty() == true) {
                    showNoInternetConnectionMessage(false)
                    showNotFoundMessage(false)
                    savedInput = null

                    if (searchHistoryInteractor.isSearchHistoryNotEmpty())
                        showSearchHistory()
                } else {
                    searchHistoryViewGroup.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchField.addTextChangedListener(searchFieldWatcher)

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery(savedInput)
                handler.removeCallbacks(searchRunnable)
            }
            false
        }

        searchField.setOnFocusChangeListener {_, hasFocus ->

            if (hasFocus && searchField.text.isEmpty()) {
                showNotFoundMessage(false)
                showNoInternetConnectionMessage(false)
                savedInput = null

                if (searchHistoryInteractor.isSearchHistoryNotEmpty()) showSearchHistory()

            } else {
                searchHistoryViewGroup.isVisible = false
            }
        }

        progressBar = findViewById(R.id.progress_bar)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(searchRunnable)
    }



    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_INPUT, savedInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInput = savedInstanceState.getString(TEXT_INPUT, null) //TEXT_EMPTY
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













//    private fun showNotFoundMessage(isTurnedOn: Boolean) {
//        notFoundPlaceholder.isVisible = isTurnedOn
//        tracks.clear()
//        searchAdapter.notifyDataSetChanged()
//    }
//
//    private fun showNoInternetConnectionMessage(isTurnedOn: Boolean) {
//        noInternetConnectionPlaceholder.isVisible = isTurnedOn
//        tracks.clear()
//        searchAdapter.notifyDataSetChanged()
//    }
//
//    private fun showSearchHistory() {
//        searchHistoryViewGroup.isVisible = true
//        rvSearch.isVisible = false
//        tracks.clear()
//        tracks.addAll(searchHistoryInteractor.getSearchHistory())
//        searchAdapter.notifyDataSetChanged()
//        rvSearchHistory.adapter = searchAdapter
//    }




    private fun observeScreenState(){
        viewModel.searchScreenState.observe(this) { state ->
            when (state) {
                is SearchScreenState.Loading -> showLoading()
                is SearchScreenState.NoInternetError -> showNoInternet()
                is SearchScreenState.NothingFound -> showNotFound()
                is SearchScreenState.SearchQueryResults -> showFoundTracks(state.foundTracks)
                is SearchScreenState.SearchHistory -> showSearchedTracks(state.searchedTracks)
            }
        }
    }

    private fun showLoading() {
        noInternetConnectionPlaceholder.isVisible = false
        notFoundPlaceholder.isVisible = false
        rvSearch.isVisible = false
        progressBar.isVisible = true
    }

    private fun showNoInternet() {
        rvSearch.isVisible = false
        progressBar.isVisible = false
        notFoundPlaceholder.isVisible = false
        noInternetConnectionPlaceholder.isVisible = true
        searchAdapter.notifyDataSetChanged()
    }

    private fun showNotFound() {
        noInternetConnectionPlaceholder.isVisible = false
        rvSearch.isVisible = false
        progressBar.isVisible = false
        notFoundPlaceholder.isVisible = true
        searchAdapter.notifyDataSetChanged()
    }

    private fun showFoundTracks(foundTracks: MutableList<Track>) {
        noInternetConnectionPlaceholder.isVisible = false
        notFoundPlaceholder.isVisible = false
        progressBar.isVisible = false
        rvSearch.isVisible = true
        searchAdapter.notifyDataSetChanged()
        rvSearch.adapter = searchAdapter
    }

    private fun showSearchedTracks(searchedTracks: MutableList<Track>) {
        noInternetConnectionPlaceholder.isVisible = false
        notFoundPlaceholder.isVisible = false
        progressBar.isVisible = false
        rvSearch.isVisible = true
        searchAdapter.notifyDataSetChanged()
        rvSearchHistory.adapter = searchAdapter
    }









    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_KEY, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun createSearchAdapter() {
        searchAdapter = SearchAdapter(tracks) {
            searchHistoryInteractor.save(it) // сюда можно передавать метод ViewModel OnTrackClick, либо так - viewModel::OnTrackClick
            startPlayerActivity(it) // сюда можно передавать синглтон-объект из PlayerActivity, который будет ее запускать
        }
    }
}