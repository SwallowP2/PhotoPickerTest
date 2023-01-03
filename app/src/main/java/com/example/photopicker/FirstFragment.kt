package com.example.photopicker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.photopicker.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<PickerViewModel>()

    private val singlePhotoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
            imageUri?.let(viewModel::setImageUri)
        }

    // this contract can receive an Integer value as input parameter in its constructor
    // Regardless of what value you set to this contract, on devices that don’t support the Photo Picker, the limit will be ignored
    private val multiplePhotoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { imageUris: List<Uri> ->
            viewModel.setImageUris(imageUris.toMutableList())
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            pickSinglePhoto()
        }

        binding.buttonTwo.setOnClickListener {
            pickMultiplePhotos()
        }

        binding.openbutton.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pickSinglePhoto() {

        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect {
                Snackbar.make(binding.root, it.imageUri.toString(), Snackbar.LENGTH_LONG).show()
                binding.imageView.setImageBitmap(viewModel.viewState.value.imageBitmap)
            }
        }
    }

    private fun pickMultiplePhotos() {
        multiplePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val uriList = StringBuilder()
                viewModel.uriList.collect {
                    for (uri in it) {
                        uriList.append(uri.toString()).append("\n")
                        binding.uriList.text = uriList
                    }
                    binding.openbutton.visibility = View.VISIBLE
                    binding.openbutton.setOnClickListener {
                        val uri = viewModel.uriList.value[0].toString()
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(uri)
                            )
                        )
                        /** replace with your own uri */ }
                }
            }
        }
    }
}