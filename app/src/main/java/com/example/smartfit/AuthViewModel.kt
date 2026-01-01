package com.example.smartfit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.AppDatabase
import com.example.smartfit.data.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val existing = userDao.getUserByEmail(email)
            if (existing == null) {
                userDao.insertUser(User(name = name, email = email, password = password))
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val user = userDao.login(email, password)
            onResult(user != null)
        }
    }
}
