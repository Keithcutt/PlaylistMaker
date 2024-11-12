package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.view_model.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : BindingFragment<FragmentFavouritesBinding>() {

    private val viewModel: FavouritesViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouritesBinding {
        return FragmentFavouritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        fun newInstance() = FavouritesFragment()
    }
}