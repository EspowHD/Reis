package com.example.reis.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Post(
        val id: String = "",
        val authorUid: String = "",
        val taggedUsers: List<String> = listOf(),
        @Exclude var authorUsername: String = "",
        @Exclude var authorProfilePictureUrl: String = "",
        val text: String = "",
        val imageUrl: String = "",
        val date: Long = 0L,
        @Exclude var isLiked: Boolean = false,
        @Exclude var isLiking: Boolean = false,
        val likedBy: List<String> = listOf()
)