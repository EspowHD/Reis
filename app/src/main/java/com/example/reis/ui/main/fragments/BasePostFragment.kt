package com.example.reis.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.example.reis.adapters.PostAdapter
import com.example.reis.other.EventObserver
import com.example.reis.ui.main.dialogs.DeletePostDialog
import com.example.reis.ui.main.viewmodels.BasePostViewModel
import com.example.reis.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract class BasePostFragment(
        layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter

    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    private var curLikedIndex: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        postAdapter.setOnLikeClickListener { post, i ->
            curLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggleLikeForPost(post)
        }

        postAdapter.setOnDeletePostClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }
    }

    private fun subscribeToObservers() {
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    curLikedIndex?.let { index ->
                        postAdapter.posts[index].isLiking = false
                        postAdapter.notifyItemChanged(index)
                    }
                    snackbar(it)
                },
                onLoading = {
                    curLikedIndex?.let { index ->
                        postAdapter.posts[index].isLiking = true
                        postAdapter.notifyItemChanged(index)
                    }
                }
        ) { isLiked ->
            curLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.posts[index].apply {
                    this.isLiked = isLiked
                    if (isLiked) likedBy += uid
                    else likedBy -= uid
                }
                postAdapter.notifyItemChanged(index)
            }
        })
        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = { snackbar(it) }
        ) { deletePost ->
            postAdapter.posts -= deletePost

        })
        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    postProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    postProgressBar.isVisible = true
                }
        ) { posts ->
            postProgressBar.isVisible = false
            postAdapter.posts = posts
        })
    }
}