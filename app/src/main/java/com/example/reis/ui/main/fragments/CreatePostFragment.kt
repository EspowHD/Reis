package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.reis.R
import com.example.reis.databinding.FragmentCreatePostBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.CreatePostViewModel
import com.example.reis.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {
    private var _binding: FragmentCreatePostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: CreatePostViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.createPostProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    binding.createPostProgressBar.isVisible = true
                }
        ){
            binding.createPostProgressBar.isVisible = false
            findNavController().popBackStack()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}