package com.example.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialButton>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }


        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        themeSwitcher.isChecked = (applicationContext as App).isDarkThemeEnabled()
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }



        val shareButton = findViewById<ImageView>(R.id.share_btn)
        shareButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        val supportButton = findViewById<ImageView>(R.id.support_btn)
        supportButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayListOf(getString(R.string.email_recipient)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
                startActivity(this)
            }
        }

        val showOfferButton = findViewById<ImageView>(R.id.offer_btn)
        showOfferButton.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply{
                data = Uri.parse(getString(R.string.offer_link))
                startActivity(this)
            }
        }
    }
}