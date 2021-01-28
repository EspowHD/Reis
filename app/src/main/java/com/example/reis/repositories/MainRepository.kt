package com.example.reis.repositories

import android.net.Uri
import com.example.reis.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String, tagged: List<String>): Resource<Any>
}