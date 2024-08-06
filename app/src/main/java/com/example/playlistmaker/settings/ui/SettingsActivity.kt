package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.setThemeSettings(checked)
        }

        binding.shareBtn.setOnClickListener{
            viewModel.onShareButtonClick()
        }

        binding.supportBtn.setOnClickListener {
            viewModel.onSupportButtonClick()
        }

        binding.offerBtn.setOnClickListener {
            viewModel.onOfferButton()
        }
    }

    private fun observeViewModel() {
        viewModel.themeSwitcherState.observe(this) {themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkThemeEnabled
        }
    }
}