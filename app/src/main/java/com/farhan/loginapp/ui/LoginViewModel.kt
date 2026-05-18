package com.farhan.loginapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhan.loginapp.data.User
import com.farhan.loginapp.data.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events

    fun onUsernameChange(value: String) { username = value }
    fun onPasswordChange(value: String) { password = value }

    fun login(repository: UserRepository) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _events.emit("LOGIN_FAILED")
                return@launch
            }
            val user = repository.getUserByUsername(username)
            if (user != null && user.password == password) {
                _events.emit("LOGIN_SUCCESS:${user.username}")
            } else {
                _events.emit("LOGIN_FAILED")
            }
        }
    }

    fun register(repository: UserRepository) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _events.emit("LOGIN_FAILED")
                return@launch
            }
            val existing = repository.getUserByUsername(username)
            if (existing != null) {
                _events.emit("LOGIN_FAILED")
                return@launch
            }
            try {
                repository.insertUser(User(username = username, password = password))
                _events.emit("REGISTER_SUCCESS")
            } catch (e: Exception) {
                _events.emit("LOGIN_FAILED")
            }
        }
    }
}
