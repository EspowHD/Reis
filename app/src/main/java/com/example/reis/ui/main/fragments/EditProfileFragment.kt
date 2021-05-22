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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.reis.R
import com.example.reis.data.entities.ProfileUpdate
import com.example.reis.databinding.FragmentEditProfileBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.EditProfileViewModel
import com.example.reis.ui.slideUpViews
import com.example.reis.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: EditProfileViewModel by viewModels()

    private var curImageUri: Uri? = null

    private var curUsername: String? = null

    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var cropContent: ActivityResultLauncher<Any?>

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uriContent
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) { uri ->
            uri?.let {
                viewModel.setCurImageUri(it)
                binding.btnUpdateProfile.isEnabled = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        val uid = FirebaseAuth.getInstance().uid!!
        viewModel.getUser(uid)
        binding.btnUpdateProfile.isEnabled = false
        binding.etUsername.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }
        binding.etDescription.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }

        binding.ivProfileImage.setOnClickListener {
            cropContent.launch(null)
        }

        binding.btnUpdateProfile.setOnClickListener {
            if (binding.etUsername.text.toString() != curUsername) {
                viewModel.isValidUsername(binding.etUsername.text.toString())
            } else {
                val uid = FirebaseAuth.getInstance().uid!!
                val username = binding.etUsername.text.toString()
                val description = binding.etDescription.text.toString()
                val profileUpdate = ProfileUpdate(uid, username, description, curImageUri)
                viewModel.updateProfile(profileUpdate)
            }
        }

        slideUpViews(
                requireContext(),
                binding.ivProfileImage,
                binding.etUsername,
                binding.etDescription,
                binding.btnUpdateProfile
        )
    }

    private fun subscribeToObservers() {
        viewModel.getUserStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.editProfileProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.editProfileProgressBar.isVisible = true }
        ) { user ->
            binding.editProfileProgressBar.isVisible = false
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
            binding.etUsername.setText(user.username)
            curUsername = user.username
            binding.etDescription.setText(user.description)
            binding.btnUpdateProfile.isEnabled = false
        })
        viewModel.curImageUri.observe(viewLifecycleOwner) { uri ->
            curImageUri = uri
            glide.load(uri).into(binding.ivProfileImage)
        }
        viewModel.validUsernameStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    snackbar(it)
                }
        ) { isValid ->
            if (isValid) {
                val uid = FirebaseAuth.getInstance().uid!!
                val username = binding.etUsername.text.toString()
                val description = binding.etDescription.text.toString()
                val profileUpdate = ProfileUpdate(uid, username, description, curImageUri)
                viewModel.updateProfile(profileUpdate)
            } else snackbar(getString(R.string.error_username_in_use))
        })
        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.editProfileProgressBar.isVisible = false
                    snackbar(it)
                    binding.btnUpdateProfile.isEnabled = true
                },
                onLoading = {
                    binding.editProfileProgressBar.isVisible = true
                    binding.btnUpdateProfile.isEnabled = false
                }
        ) {
            binding.editProfileProgressBar.isVisible = false
            binding.btnUpdateProfile.isEnabled = false
            snackbar(requireContext().getString(R.string.profile_updated))
        })
    }
}