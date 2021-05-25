package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.reis.R
import com.example.reis.data.entities.User
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.viewmodels.OthersProfileViewModel

class OthersProfileFragment : ProfileFragment() {

    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    override val viewModel: OthersProfileViewModel by viewModels()

    private var curUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.ibFollow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            binding.ibFollow.isVisible = true
            binding.ibEditProfile.isVisible = false
            setupToggleFollowButton(it)
            curUser = it
        })
        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            curUser?.isFollowing = it
            setupToggleFollowButton(curUser ?: return@EventObserver)
        })
    }

    private fun setupToggleFollowButton(user: User) {
        binding.ibFollow.setImageResource(
                if (user.isFollowing) {
                    R.drawable.ic_follow
                } else R.drawable.ic_follow_border
        )
    }
}