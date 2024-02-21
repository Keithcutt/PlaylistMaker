package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private var savedInput: String? = null
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
}