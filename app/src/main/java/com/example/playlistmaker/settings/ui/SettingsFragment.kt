package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.setThemeSettings(checked)
        }

        binding.shareBtn.setOnClickListener {
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
        viewModel.themeSwitcherState.observe(viewLifecycleOwner) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkThemeEnabled
        }
    }
}