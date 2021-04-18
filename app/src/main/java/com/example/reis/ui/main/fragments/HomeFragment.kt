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
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.adapters.SimplePostAdapter
import com.example.reis.databinding.FragmentHomeBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.HomeViewModel
import com.example.reis.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var simplePostAdapter: SimplePostAdapter

    private var postProgressBar: ProgressBar? = null

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postProgressBar = binding.allPostsProgressBar
        setupRecyclerView()
        subscribeToObservers()

        simplePostAdapter.setOnPostClickListener { post ->
            findNavController()
                    .navigate(
                            ViewPostFragmentDirections.globalActionToViewPostFragment(post.id)
                    )
        }

        simplePostAdapter.setOnUserClickListener { user ->
            findNavController()
                    .navigate(
                            HomeFragmentDirections.globalActionToOthersProfileFragment(user)
                    )
        }
    }

    private fun setupRecyclerView() = binding.rvAllPosts.apply {
        adapter = simplePostAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.posts.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.allPostsProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    binding.allPostsProgressBar.isVisible = true
                }
        ) { posts ->
            binding.allPostsProgressBar.isVisible = false
            simplePostAdapter.posts = posts
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}