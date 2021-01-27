package com.example.reis.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.reis.R
import com.example.reis.databinding.FragmentRegisterBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.auth.AuthViewModel
import com.example.reis.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    //ViewBinding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    //variables
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        subscribeToObservers()

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                    binding.etEmail.text.toString(),
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etRepeatPassword.text.toString()
            )
        }

        binding.tvLogin.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.registerProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.registerProgressBar.isVisible = true }
        ) {
            binding.registerProgressBar.isVisible = false
            snackbar(getString(R.string.success_registration))
        })
    }
}