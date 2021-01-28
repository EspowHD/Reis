package com.example.reis.ui.main.viewmodels

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reis.other.Event
import com.example.reis.other.Resource
import com.example.reis.repositories.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePostViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val applicationContext: Context,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createPostStatus = MutableLiveData<Event<Resource<Any>>>()
    val createPostStatus: LiveData<Event<Resource<Any>>> = _createPostStatus

    fun createPost(imageUri: Uri, text: String, taggedUsers: List<String>) {
        _createPostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.createPost(imageUri, text, taggedUsers)
            _createPostStatus.postValue(Event(result))
        }
    }
}