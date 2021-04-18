package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.data.entities.Post
import com.example.reis.databinding.FragmentViewPostBinding
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.dialogs.DeletePostDialog
import com.example.reis.ui.main.viewmodels.ViewPostViewModel
import com.example.reis.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewPostFragment : Fragment(R.layout.fragment_view_post) {

    @Inject
    lateinit var glide: RequestManager

    private val args: ViewPostFragmentArgs by navArgs()

    private val viewModel: ViewPostViewModel by viewModels()

    private var post: Post? = null

    private var _binding: FragmentViewPostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.ibLike.setOnClickListener {
            viewModel.toggleLikeForPost(post!!)
        }
        binding.ibDeletePost.setOnClickListener {
            DeletePostDialog().apply {
                setPositiveListener {
                    viewModel.deletePost(post!!)
                }
            }.show(childFragmentManager, null)
        }
        viewModel.loadPost(args.postid)
    }

    private fun subscribeToObservers() {
        viewModel.postMeta.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    binding.postMetaProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { binding.postMetaProgressBar.isVisible = true }
        ) { inPost ->
            post = inPost
            binding.postMetaProgressBar.isVisible = false
            glide.load(post!!.imageUrl).into(binding.ivPostImage)
            glide.load(post!!.authorProfilePictureUrl).into(binding.ivAuthorProfileImage)
            binding.apply {
                tvPostAuthor.text = post!!.authorUsername
                tvPostText.text = post!!.text
                val likeString = "${post!!.likedBy.size} likes"
                tvLikedBy.text = likeString
                val uid = FirebaseAuth.getInstance().uid!!
                ibDeletePost.isVisible = uid == post!!.authorUid
                ibLike.setImageResource(
                        if (post!!.isLiked) {
                            R.drawable.ic_like
                        } else R.drawable.ic_like_border
                )
            }
        })

        viewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = { snackbar(it) }
        ) {
            findNavController().popBackStack()
        })

        viewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    post!!.isLiking = false
                    snackbar(it)
                },
                onLoading = {
                    post!!.isLiking = true
                }
        ) { isLiked ->
            val uid = FirebaseAuth.getInstance().uid!!
            post!!.isLiked = isLiked
            if (isLiked) post!!.likedBy += uid
            else post!!.likedBy -= uid
            //Update post UI elements
            binding.ibLike.setImageResource(
                    if (post!!.isLiked) {
                        R.drawable.ic_like
                    } else R.drawable.ic_like_border
            )
            val likeString = "${post!!.likedBy.size} likes"
            binding.tvLikedBy.text = likeString
        })
    }
}