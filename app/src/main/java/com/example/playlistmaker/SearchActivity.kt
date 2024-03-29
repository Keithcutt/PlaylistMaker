package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.retrofit.ITunesApi
import com.example.playlistmaker.retrofit.TrackResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var savedInput: String? = null
    private lateinit var notFoundPlaceholder: LinearLayout
    private lateinit var noInternetConnectionPlaceholder: LinearLayout
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var rvSearch: RecyclerView
    private lateinit var updateButton: MaterialButton

    private val foundTracks: MutableList<Track> = mutableListOf()


    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApiService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<MaterialButton>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.search_bar)
        savedInstanceState ?: inputEditText.setText(savedInput)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        clearButton.setOnClickListener {
            inputEditText.setText("")

            foundTracks.clear()
            searchAdapter.notifyDataSetChanged()
            rvSearch.isVisible = false
            showNotFoundMessage(false)
            showNoInternetConnectionMessage(false)


            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }


        val searchBarWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    savedInput = s.toString()

                }
                clearButton.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(searchBarWatcher)

        rvSearch = findViewById<RecyclerView>(R.id.rv_search)
        rvSearch.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        searchAdapter = SearchAdapter(foundTracks)
        rvSearch.adapter = searchAdapter





        notFoundPlaceholder = findViewById(R.id.placeholder_not_found)
        noInternetConnectionPlaceholder = findViewById(R.id.placeholder_no_internet)

        inputEditText.setOnEditorActionListener {_, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
                true
            }
            false
        }

        updateButton = findViewById(R.id.update_btn)
        updateButton.setOnClickListener {
            searchQuery()
        }
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
        savedInput = savedInstanceState.getString(TEXT_INPUT, TEXT_DEF)
    }

    private companion object {
        const val TEXT_INPUT = "TEXT_INPUT"
        const val TEXT_DEF = ""
    }

    private fun showNotFoundMessage(on: Boolean) {
        notFoundPlaceholder.isVisible = on
        foundTracks.clear()
        searchAdapter.notifyDataSetChanged()
    }

    private fun showNoInternetConnectionMessage(on: Boolean) {
        noInternetConnectionPlaceholder.isVisible = on
        foundTracks.clear()
        searchAdapter.notifyDataSetChanged()

    }

    private fun searchQuery() {
        savedInput?.let {
            showNotFoundMessage(false)
            showNoInternetConnectionMessage(false)
            iTunesApiService.search(it).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                rvSearch.isVisible = true
                                foundTracks.clear()
                                foundTracks.addAll(response.body()?.results!!)
                                searchAdapter.notifyDataSetChanged()
                            } else {
                                showNotFoundMessage(true)
                                rvSearch.isVisible = false
                            }
                        }
                        else -> {
                            showNoInternetConnectionMessage(true)
                            rvSearch.isVisible = false
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showNoInternetConnectionMessage(true)
                    rvSearch.isVisible = false
                }
            })
        }
    }
}