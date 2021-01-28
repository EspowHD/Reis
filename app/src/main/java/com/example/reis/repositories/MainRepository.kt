package com.example.reis.repositories

import android.net.Uri
import com.example.reis.data.entities.User
import com.example.reis.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String, taggedUsers: List<String>): Resource<Any>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>
}