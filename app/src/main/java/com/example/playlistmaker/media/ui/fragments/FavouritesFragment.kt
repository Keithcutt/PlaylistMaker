package com.example.playlistmaker.media.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : BindingFragment<FragmentFavouritesBinding>() {

    private val viewModel: FavouritesViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouritesBinding {
        return FragmentFavouritesBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance() = FavouritesFragment()
    }
}