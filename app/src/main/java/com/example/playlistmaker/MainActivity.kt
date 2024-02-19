package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<MaterialButton>(R.id.search_btn)
        searchButton.setOnClickListener {
            val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val mediaButton = findViewById<MaterialButton>(R.id.media_btn)
        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val settingsButton = findViewById<MaterialButton>(R.id.settings_btn)
        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}