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
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.adapters.SimplePostAdapter
import com.example.reis.databinding.FragmentHomeBinding
import com.example.reis.ui.main.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var simplePostAdapter: SimplePostAdapter

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
        setupRecyclerView()

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

        lifecycleScope.launch {
            viewModel.pagingFlow.collect {
                simplePostAdapter.submitData(it)
                binding.tvNoPostHelper.isVisible = simplePostAdapter.itemCount == 0
            }
        }

        lifecycleScope.launch {
            simplePostAdapter.loadStateFlow.collectLatest {
                binding.allPostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
                binding.tvNoPostHelper.isVisible = !binding.allPostsProgressBar?.isVisible
            }
        }
    }

    private fun setupRecyclerView() = binding.rvAllPosts.apply {
        adapter = simplePostAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}