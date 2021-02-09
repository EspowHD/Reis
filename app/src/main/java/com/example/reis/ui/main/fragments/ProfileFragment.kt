package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reis.R
import com.example.reis.databinding.FragmentProfileBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.BasePostViewModel
import com.example.reis.ui.main.viewmodels.ProfileViewModel
import com.example.reis.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override val postProgressBar: ProgressBar
        get() = binding.profilePostsProgressBar

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        binding.btnToggleFollow.isVisible = false
        viewModel.loadProfile(uid)
    }

    private fun setupRecyclerView() = binding.rvPosts.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.profileMetaProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.profileMetaProgressBar.isVisible = true }
        ) { user ->
            binding.profileMetaProgressBar.isVisible = false
            binding.tvUsername.text = user.username
            binding.tvProfileDescription.text = if (user.description.isEmpty()) {
                requireContext().getString(R.string.no_description)
            } else user.description
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
        })
    }
}