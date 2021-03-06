package com.example.reis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.data.entities.Post
import com.example.reis.databinding.ItemSimplePostBinding
import javax.inject.Inject

class SimplePostAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Post, SimplePostAdapter.PostViewHolder>(Companion) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSimplePostBinding.bind(itemView)
        val ivPostImage: ImageView = binding.ivPostImage
        val ivAuthorProfileImage: ImageView = binding.ivAuthorProfileImage
        val tvPostTitle: TextView = binding.tvPostTitle
    }

    companion object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_simple_post,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.apply {
            glide.load(post.imageUrl).into(ivPostImage)
            glide.load(post.authorProfilePictureUrl).into(ivAuthorProfileImage)
            tvPostTitle.text = post.text

            ivAuthorProfileImage.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }

            ivPostImage.setOnClickListener {
                onPostClickListener?.let { click ->
                    click(post)
                }
            }

            tvPostTitle.setOnClickListener {
                onPostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    private var onUserClickListener: ((String) -> Unit)? = null
    private var onPostClickListener: ((Post) -> Unit)? = null

    fun setOnPostClickListener(listener: (Post) -> Unit) {
        onPostClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }


}