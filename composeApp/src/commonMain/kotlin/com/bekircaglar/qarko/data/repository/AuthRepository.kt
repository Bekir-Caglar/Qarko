package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.User
import com.bekircaglar.qarko.data.model.UserAuth
import com.bekircaglar.qarko.data.model.UserProfile
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class AuthRepository : IAuthRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    override val currentUserFlow: Flow<User?> = auth.authStateChanged.map { firebaseUser ->
        firebaseUser?.let { getUserById(it.uid) }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val uid = authResult.user?.uid ?: throw Exception("Giriş başarısız: UID bulunamadı")
            val user = getUserById(uid) ?: throw Exception("Kullanıcı verisi bulunamadı")
            
            if (!user.isEnabled) {
                auth.signOut()
                throw Exception("Hesabınız devre dışı bırakılmış")
            }

            if (user.role != "CUSTOMER") {
                auth.signOut()
                throw Exception("Sadece müşteri hesapları giriş yapabilir")
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithPhone(phone: String, password: String): Result<User> {
        return try {
            // 1. Telefon numarasına sahip kullanıcıyı bul
            val querySnapshot = usersCollection.where("phoneNumber", equalTo = phone).get()
            if (querySnapshot.documents.isEmpty()) {
                throw Exception("Bu telefon numarasına ait bir hesap bulunamadı")
            }
            
            val userDoc = querySnapshot.documents.first()
            val userEmail = userDoc.data<User>().email
            
            if (userEmail.isEmpty()) {
                throw Exception("Kullanıcı e-posta adresi bulunamadı")
            }

            // 2. Bulunan e-posta ile giriş yap
            return loginWithEmail(userEmail, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerWithEmailAndPhone(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUid = authResult.user?.uid ?: throw Exception("Kullanıcı oluşturulamadı")

            val now = Clock.System.now().toEpochMilliseconds()
            val newUser = User(
                id = firebaseUid,
                firebaseUid = firebaseUid,
                email = email,
                phoneNumber = phone,
                displayName = name,
                role = "CUSTOMER",
                isEnabled = true,
                auth = UserAuth(
                    providers = listOf("email", "phone"),
                    primaryProvider = "email",
                    email = email,
                    phoneNumber = phone,
                    isEmailVerified = false,
                    isPhoneVerified = false
                ),
                profile = UserProfile(
                    displayName = name,
                    firstName = name.split(" ").firstOrNull() ?: "",
                    lastName = name.split(" ").drop(1).joinToString(" ")
                ),
                createdAt = now,
                updatedAt = now
            )

            usersCollection.document(firebaseUid).set(newUser)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        return auth.currentUser?.let { getUserById(it.uid) }
    }

    private suspend fun getUserById(uid: String): User? {
        return try {
            val doc = usersCollection.document(uid).get()
            if (doc.exists) doc.data<User>() else null
        } catch (e: Exception) {
            null
        }
    }
}
