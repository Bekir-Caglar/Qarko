package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    val currentUserFlow: Flow<User?>
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun loginWithPhone(phone: String, password: String): Result<User> // Yeni metod
    suspend fun registerWithEmailAndPhone(name: String, email: String, phone: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
}
