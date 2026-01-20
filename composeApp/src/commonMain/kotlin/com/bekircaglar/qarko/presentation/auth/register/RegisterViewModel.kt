package com.bekircaglar.qarko.presentation.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    fun register() {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            error = "Lütfen tüm alanları doldurun"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            authRepository.registerWithEmailAndPhone(name, email, phone, password)
                .onSuccess { user ->
                    UserManager.updateUser(user)
                    registerSuccess = true
                }
                .onFailure {
                    error = it.message ?: "Kayıt işlemi başarısız"
                }
            isLoading = false
        }
    }

    fun clearError() {
        error = null
    }
}
