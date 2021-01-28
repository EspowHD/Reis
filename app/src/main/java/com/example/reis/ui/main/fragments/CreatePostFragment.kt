package com.example.reis.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.databinding.FragmentCreatePostBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.CreatePostViewModel
import com.example.reis.ui.snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {
    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentCreatePostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: CreatePostViewModel by viewModels()

    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                    .setAspectRatio(16, 9)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }
    }

    private var curImageUri: Uri? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.setCurImageUri(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.btnSetPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.ivPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.btnPost.setOnClickListener {
            curImageUri?.let { uri ->
                viewModel.createPost(uri, binding.etPostDescription.text.toString(), listOf())
            } ?: snackbar(getString(R.string.error_no_image_chosen))
        }
    }

    private fun subscribeToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            binding.btnSetPostImage.isVisible = false
            glide.load(curImageUri).into(binding.ivPostImage)
        }
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.createPostProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    binding.createPostProgressBar.isVisible = true
                }
        ) {
            binding.createPostProgressBar.isVisible = false
            findNavController().popBackStack()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}