package com.bekircaglar.qarko.presentation.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var phone by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun loginWithEmail() {
        if (email.isBlank() || password.isBlank()) {
            error = "Lütfen e-posta ve şifrenizi girin"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            authRepository.loginWithEmail(email, password)
                .onSuccess { user ->
                    UserManager.updateUser(user)
                    loginSuccess = true
                }
                .onFailure {
                    error = it.message ?: "Giriş yapılamadı"
                }
            isLoading = false
        }
    }

    fun loginWithPhone() {
        if (phone.isBlank() || password.isBlank()) {
            error = "Lütfen telefon numaranızı ve şifrenizi girin"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            // Telefon numarasını temizle (başındaki +90 veya boşlukları handle etmek gerekebilir)
            val cleanedPhone = phone.replace(" ", "").replace("+", "")
            
            authRepository.loginWithPhone(cleanedPhone, password)
                .onSuccess { user ->
                    UserManager.updateUser(user)
                    loginSuccess = true
                }
                .onFailure {
                    error = it.message ?: "Giriş yapılamadı"
                }
            isLoading = false
        }
    }

    fun clearError() {
        error = null
    }
}
