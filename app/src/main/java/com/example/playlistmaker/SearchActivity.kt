package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val TEXT_INPUT = "TEXT_INPUT"
        const val TEXT_EMPTY = ""
        const val SUCCESSFUL_RESPONSE = 200
        const val SEARCH_HISTORY_PREFERENCES = "search_history_shared_preferences"
        const val TRACK_KEY = "track"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
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
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryViewGroup: LinearLayout

    private var savedInput: String? = null
    private var tracks: MutableList<Track> = mutableListOf() //Dto

//    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://itunes.apple.com")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()

//    private val iTunesApiService = retrofit.create(ITunesApiService::class.java)

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery(savedInput) }
    private var isClickAllowed = true

    private val tracksInteractor = Creator.provideTracksInteractor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<MaterialButton>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        rvSearch = findViewById(R.id.rv_search)
        createSearchAdapter()

        notFoundPlaceholder = findViewById(R.id.placeholder_not_found)
        noInternetConnectionPlaceholder = findViewById(R.id.placeholder_no_internet)
        updateButton = findViewById(R.id.update_btn)
        updateButton.setOnClickListener {
             searchQuery(savedInput)
        }

        createSearchHistoryHandler()
        rvSearchHistory = findViewById(R.id.rv_search_history)
        searchHistoryViewGroup = findViewById(R.id.search_history_viewgroup)
        val clearHistoryButton = findViewById<MaterialButton>(R.id.clear_history_btn)
        clearHistoryButton.setOnClickListener{
            searchHistory.clear()
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

                    if (searchHistory.isSearchHistoryNotEmpty())
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
                // true
            }
            false
        }

        searchField.setOnFocusChangeListener {_, hasFocus ->

            if (hasFocus && searchField.text.isEmpty()) {
                showNotFoundMessage(false)
                showNoInternetConnectionMessage(false)
                savedInput = null

                if (searchHistory.isSearchHistoryNotEmpty()) showSearchHistory()

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

    private fun showNotFoundMessage(isTurnedOn: Boolean) {
        notFoundPlaceholder.isVisible = isTurnedOn
        tracks.clear()
        searchAdapter.notifyDataSetChanged()
    }

    private fun showNoInternetConnectionMessage(isTurnedOn: Boolean) {
        noInternetConnectionPlaceholder.isVisible = isTurnedOn
        tracks.clear()
        searchAdapter.notifyDataSetChanged()

    }

    private fun createSearchHistoryHandler() {
        val sharedPreferences = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
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
            searchHistory.save(it)
            startPlayerActivity(it)
        }
    }

    private fun showSearchHistory() {
        searchHistoryViewGroup.isVisible = true
        rvSearch.isVisible = false
        tracks.clear()
        tracks.addAll(searchHistory.getSearchHistory())
        searchAdapter.notifyDataSetChanged()
        rvSearchHistory.adapter = searchAdapter
    }

//    private fun searchQuery(requestText: String?) {
//        if (requestText?.isBlank() == false) requestText.let {
//            showNotFoundMessage(false)
//            showNoInternetConnectionMessage(false)
//            rvSearch.isVisible = false
//            progressBar.isVisible = true
//
//            iTunesApiService.searchTracks(it).enqueue(object : Callback<TrackSearchResponse> {
//                override fun onResponse(
//                    call: Call<TrackSearchResponse>,
//                    response: Response<TrackSearchResponse>
//                ) {
//                    progressBar.isVisible = false
//                    when (response.code()) {
//                        SUCCESSFUL_RESPONSE -> {
//                            if (response.body()?.results?.isNotEmpty() == true) {
//                                rvSearch.isVisible = true
//                                foundTracks.clear()
//                                foundTracks.addAll(response.body()?.results!!)
//                                rvSearch.adapter = searchAdapter
//                                searchAdapter.notifyDataSetChanged()
//
//                            } else {
//                                showNotFoundMessage(true)
//                                rvSearch.isVisible = false
//                            }
//                        }
//                        else -> {
//                            showNoInternetConnectionMessage(true)
//                            rvSearch.isVisible = false
//                            progressBar.isVisible = false
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
//                    showNoInternetConnectionMessage(true)
//                    rvSearch.isVisible = false
//                    progressBar.isVisible = false
//                }
//            })
//        }
//    }

    private fun searchQuery(expression: String?) {
        if (expression?.isBlank() == false) expression.let {

            showNotFoundMessage(false)
            showNoInternetConnectionMessage(false)
            rvSearch.isVisible = false
            progressBar.isVisible = true

            tracksInteractor.searchTracks(it, object : TracksInteractor.TracksConsumer{
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post{
                        progressBar.isVisible = false

                        if (foundTracks != null) {
                            rvSearch.isVisible = true
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            rvSearch.adapter = searchAdapter
                            searchAdapter.notifyDataSetChanged()
                        }
                        if (errorMessage != null) {
                            showNoInternetConnectionMessage(true)
                            rvSearch.isVisible = false
                            progressBar.isVisible = false
                        } else if (tracks.isEmpty()) {
                            showNotFoundMessage(true)
                            rvSearch.isVisible = false
                        }

//                        if (errorMessage != null) {
//                            showMessage(activity.getString(R.string.something_went_wrong), errorMessage)
//                        } else if (movies.isEmpty()) {
//                            showMessage(activity.getString(R.string.nothing_found), "")
//                        } else {
//                            hideMessage()
//                        }
                    }
                }

            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed( { isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}