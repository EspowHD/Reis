package com.example.reis.repositories

import android.net.Uri
import com.example.reis.data.entities.Comment
import com.example.reis.data.entities.Post
import com.example.reis.data.entities.ProfileUpdate
import com.example.reis.data.entities.User
import com.example.reis.other.Resource

interface MainRepository {

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun searchUser(query: String): Resource<List<User>>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun createPost(imageUri: Uri, text: String, taggedUsers: List<String>): Resource<Any>

    suspend fun getPostsForFollows(): Resource<List<Post>>

    suspend fun getPost(pid: String): Resource<Post>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun createComment(commentText: String, postId: String): Resource<Comment>

    suspend fun deleteComment(comment: Comment): Resource<Comment>

    suspend fun getCommentsForPost(postId: String): Resource<List<Comment>>

    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>

    suspend fun updateProfilePicture(uid: String, imageUri: Uri): Uri?

    suspend fun checkValidUsername(username: String): Resource<Boolean>
}