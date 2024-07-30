package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModelFactory


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by lazy {
        ViewModelProvider(this, SettingsViewModelFactory())[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()


        binding.shareBtn.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        binding.supportBtn.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayListOf(getString(R.string.email_recipient)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
                startActivity(this)
            }
        }

        binding.offerBtn.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply{
                data = Uri.parse(getString(R.string.offer_link))
                startActivity(this)
            }
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.setThemeSettings(checked)
        }
    }

    private fun observeViewModel() {
        viewModel.themeSwitcherState.observe(this) {themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkThemeEnabled
        }
    }
}