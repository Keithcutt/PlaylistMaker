package com.example.playlistmaker.media.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment(private val fromPlayer: Boolean = false) :
    BindingFragment<FragmentNewPlaylistBinding>() {

    companion object {
        private const val PLAYLIST_COVER_CORNER_RADIUS = 8f
        fun newInstance(fromPlayer: Boolean) = NewPlaylistFragment(fromPlayer)
        const val RESULT_KEY = "result"
    }

    private var playlistName: String = ""
    private var isAnythingProvided = false
    open val viewModel: NewPlaylistViewModel by viewModel()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val dialogAlert: MaterialAlertDialogBuilder by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(R.string.dialog_alert_title)
            .setMessage(R.string.dialog_alert_message)
            .setNegativeButton(R.string.dialog_alert_neutral) { _, _ ->

            }
            .setPositiveButton(R.string.dialog_alert_positive) { _, _ ->
                escapeFragment()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            pickPlaylistCover(uri)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupTextWatchers()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            showDialogAlert(isAnythingProvided)
        }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()

            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created).format(playlistName),
                Toast.LENGTH_SHORT
            ).show()
            escapeFragment()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialogAlert(isAnythingProvided)
                }
            }
        )
    }

    private fun setupTextWatchers() {
        binding.nameTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.createButton.isEnabled = !s.isNullOrBlank()

                playlistName = binding.nameTextField.editText?.text.toString()
                viewModel.updatePlaylistName(playlistName)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.descriptionTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val description = binding.descriptionTextField.editText?.text.toString()
                if (description.isNotBlank()) {
                    viewModel.updatePlaylistDescription(description)
                } else {
                    viewModel.updatePlaylistDescription(null)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun pickPlaylistCover(uri: Uri?) {
        if (uri != null) {
            binding.playlistCover.foreground = null
            binding.playlistCover.background = null

            Glide.with(this@NewPlaylistFragment)
                .load(uri)
                .apply(
                    RequestOptions().transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(dpToPx(PLAYLIST_COVER_CORNER_RADIUS, requireContext()))
                        )
                    )
                )
                .into(binding.playlistCover)
            viewModel.setPlaylistCover(uri)
        }
    }

    private fun observeViewModel() {
        viewModel.isAnythingProvided.observe(viewLifecycleOwner) { isAnythingProvided ->
            this.isAnythingProvided = isAnythingProvided
        }
    }

    private fun showDialogAlert(shouldShowDialog: Boolean) {
        if (shouldShowDialog) {
            dialogAlert.show()
        } else {
            escapeFragment()
        }
    }

    protected fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun escapeFragment() {
        val result = Bundle()
        if (fromPlayer) {
            parentFragmentManager.setFragmentResult(RESULT_KEY, result)
        } else {
            findNavController().navigateUp()
        }

    }
}