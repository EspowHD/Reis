package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.adapters.GridPostAdapter
import com.example.reis.databinding.FragmentProfileBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.ProfileViewModel
import com.example.reis.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gridPostAdapter: GridPostAdapter


    protected val viewModel: ProfileViewModel by viewModels()

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    protected val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        binding.ibFollow.isVisible = false
        binding.ibEditProfile.isVisible = true
        viewModel.loadProfile(uid)

        binding.ibEditProfile.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.globalActionToEditProfileFragment())
        }

        gridPostAdapter.setOnPostClickListener { post ->
            findNavController()
                .navigate(
                    ViewPostFragmentDirections.globalActionToViewPostFragment(post.id)
                )
        }

        lifecycleScope.launch {
            viewModel.getPagingFlow(uid).collect {
                gridPostAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            gridPostAdapter.loadStateFlow.collectLatest {
                binding.profilePostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
            }
        }
    }

    private fun setupRecyclerView() = binding.rvPosts.apply {
        adapter = gridPostAdapter
        itemAnimator = null
        layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
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
            if (user.description.isEmpty()) {
                binding.tvProfileDescription.isVisible = false
            } else binding.tvProfileDescription.text = user.description
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}