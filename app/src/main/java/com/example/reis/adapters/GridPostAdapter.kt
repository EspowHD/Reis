package com.example.reis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.reis.R
import com.example.reis.data.entities.Post
import com.example.reis.databinding.ItemGridPostBinding
import javax.inject.Inject

class GridPostAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Post, GridPostAdapter.PostViewHolder>(Companion) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemGridPostBinding.bind(itemView)
        val ivPostImage: ImageView = binding.ivPostImage
    }

    companion object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_grid_post,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.apply {
            glide.load(post.imageUrl).into(ivPostImage)

            ivPostImage.setOnClickListener {
                onPostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    private var onPostClickListener: ((Post) -> Unit)? = null

    fun setOnPostClickListener(listener: (Post) -> Unit) {
        onPostClickListener = listener
    }
}