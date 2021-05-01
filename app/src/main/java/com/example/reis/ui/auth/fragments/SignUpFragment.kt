package com.example.reis.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.reis.R
import com.example.reis.databinding.FragmentSignUpBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.auth.AuthViewModel
import com.example.reis.ui.main.MainActivity
import com.example.reis.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    //ViewBinding
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    //variables
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        subscribeToObservers()

        binding.btnRegister.setOnClickListener {
            viewModel.isValidUsername(binding.etUsername.text.toString())
        }

        binding.tvGoBack.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFrgamentToAuthFragment()
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.validUsernameStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    snackbar(it)
                }
        ) { isValid ->
            if (isValid) {
                viewModel.register(
                        binding.etEmail.text.toString(),
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString(),
                        binding.etRepeatPassword.text.toString()
                )
            } else snackbar(getString(R.string.error_username_in_use))
        })
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.registerProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.registerProgressBar.isVisible = true }
        ) {
            binding.registerProgressBar.isVisible = false
            snackbar(getString(R.string.success_registration))
            Intent(requireContext(), MainActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}