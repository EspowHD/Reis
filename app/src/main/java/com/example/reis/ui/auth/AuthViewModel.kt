package com.example.reis.ui.auth

import android.content.Context
import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reis.R
import com.example.reis.other.Constants.LOWER_CASE_LETTER_REGEX
import com.example.reis.other.Constants.MAX_USERNAME_LENGTH
import com.example.reis.other.Constants.MIN_PASSWORD_LENGTH
import com.example.reis.other.Constants.MIN_USERNAME_LENGTH
import com.example.reis.other.Constants.SPECIAL_CHARACTERS_REGEX
import com.example.reis.other.Constants.UPPER_CASE_LETTER_REGEX
import com.example.reis.other.Event
import com.example.reis.other.Resource
import com.example.reis.repositories.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
        private val repository: AuthRepository,
        private val applicationContext: Context,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val registerStatus: LiveData<Event<Resource<AuthResult>>> = _registerStatus

    private val _validUsernameStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val validUsernameStatus: LiveData<Event<Resource<Boolean>>> = _validUsernameStatus

    private val _loginStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val loginStatus: LiveData<Event<Resource<AuthResult>>> = _loginStatus

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _loginStatus.postValue(Event(Resource.Error(error)))
        } else {
            _loginStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.login(email, password)
                _loginStatus.postValue(Event(result))
            }
        }
    }

    fun register(email: String, username: String, password: String, repeatedPassword: String) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatedPassword) {
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < MIN_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
        } else if (username.length > MAX_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGTH)
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            applicationContext.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGTH)
        } else if (!password.contains(LOWER_CASE_LETTER_REGEX)) {
            applicationContext.getString(R.string.error_password_no_lower_case)
        } else if (!password.contains(UPPER_CASE_LETTER_REGEX)) {
            applicationContext.getString(R.string.error_password_no_upper_case)
        } else if (!password.contains(SPECIAL_CHARACTERS_REGEX)) {
            applicationContext.getString(R.string.error_password_no_special_characters)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _registerStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.register(email, username, password)
            repository.login(email, password)
            _registerStatus.postValue(Event(result))
        }
    }

    fun isValidUsername(username: String) {
        _validUsernameStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.checkValidUsername(username)
            _validUsernameStatus.postValue(Event(result))
        }
    }
}

