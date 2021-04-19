package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import javax.inject.Inject

@AndroidEntryPoint
open class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gridPostAdapter: GridPostAdapter

    private var postProgressBar: ProgressBar? = null

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
        postProgressBar = binding.profilePostsProgressBar

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

        viewModel.posts.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    postProgressBar!!.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    postProgressBar!!.isVisible = true
                }
        ) { posts ->
            postProgressBar!!.isVisible = false
            gridPostAdapter.posts = posts
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}