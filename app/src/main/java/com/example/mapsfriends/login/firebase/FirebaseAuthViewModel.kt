package com.example.mapsfriends.login.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mapsfriends.FirebaseUserRepository
import com.example.mapsfriends.User
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val repository = FirebaseUserRepository()
    private val auth = Firebase.auth

    fun signUp(
        email: String,
        password: String,
        onSignUpSuccess: () -> Unit,
        onSignUpFailure: (String) -> Unit
    ) {
        try {
            Log.d("FirebaseAuthViewModel", "Начало процесса регистрации")
            
            if (email.isBlank() || password.isBlank()) {
                Log.e("FirebaseAuthViewModel", "Email или пароль пусты")
                onSignUpFailure("Email и пароль не могут быть пустыми")
                return
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = it.result.user?.uid!!
                        Log.d("FirebaseAuthViewModel", "Пользователь успешно создан в Firebase: $userId")

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                saveUserInFirebase(
                                    token = "",
                                    User(
                                        userId = userId,
                                        username = email,
                                        avatarUrl = "",
                                        friends = emptyList(),
                                        location = GeoPoint(0.0, 0.0)
                                    )
                                )
                                Log.d("FirebaseAuthViewModel", "Данные пользователя сохранены в Firebase")
                                
                                withContext(Dispatchers.Main) {
                                    onSignUpSuccess()
                                }
                            } catch (e: Exception) {
                                Log.e("FirebaseAuthViewModel", "Ошибка при сохранении данных пользователя", e)
                                withContext(Dispatchers.Main) {
                                    onSignUpFailure("Ошибка при сохранении данных пользователя: ${e.message}")
                                }
                            }
                        }
                    } else {
                        Log.e("FirebaseAuthViewModel", "Ошибка при создании пользователя: ${it.exception?.message}")
                        onSignUpFailure(it.exception?.message ?: "Ошибка при регистрации")
                    }
                }
                .addOnFailureListener {
                    Log.e("FirebaseAuthViewModel", "Ошибка при регистрации", it)
                    onSignUpFailure(it.message ?: "Ошибка при регистрации")
                }
        } catch (e: Exception) {
            Log.e("FirebaseAuthViewModel", "Критическая ошибка при регистрации", e)
            onSignUpFailure("Критическая ошибка при регистрации: ${e.message}")
        }
    }

    fun signIn(
        email: String,
        password: String,
        onSignInSuccess: () -> Unit,
        onSignInFailure: (String) -> Unit
    ) {
        try {
            Log.d("FirebaseAuthViewModel", "Начало процесса входа")
            
            if (email.isBlank() || password.isBlank()) {
                Log.e("FirebaseAuthViewModel", "Email или пароль пусты")
                onSignInFailure("Email и пароль не могут быть пустыми")
                return
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = it.result.user?.uid!!
                        Log.d("FirebaseAuthViewModel", "Пользователь успешно вошел в Firebase: $userId")

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val user = repository.getUserById(userId)
                                if (user == null) {
                                    Log.d("FirebaseAuthViewModel", "Пользователь не найден в базе данных, создаем нового")
                                    saveUserInFirebase(
                                        token = "",
                                        User(
                                            userId = userId,
                                            username = email,
                                            avatarUrl = "",
                                            friends = emptyList(),
                                            location = GeoPoint(0.0, 0.0)
                                        )
                                    )
                                } else {
                                    Log.d("FirebaseAuthViewModel", "Пользователь найден в базе данных: ${user.username}")
                                }
                                
                                withContext(Dispatchers.Main) {
                                    onSignInSuccess()
                                }
                            } catch (e: Exception) {
                                Log.e("FirebaseAuthViewModel", "Ошибка при получении данных пользователя", e)
                                withContext(Dispatchers.Main) {
                                    onSignInFailure("Ошибка при получении данных пользователя: ${e.message}")
                                }
                            }
                        }
                    } else {
                        Log.e("FirebaseAuthViewModel", "Ошибка при входе: ${it.exception?.message}")
                        onSignInFailure(it.exception?.message ?: "Ошибка при входе")
                    }
                }
                .addOnFailureListener {
                    Log.e("FirebaseAuthViewModel", "Ошибка при входе", it)
                    onSignInFailure(it.message ?: "Ошибка при входе")
                }
        } catch (e: Exception) {
            Log.e("FirebaseAuthViewModel", "Критическая ошибка при входе", e)
            onSignInFailure("Критическая ошибка при входе: ${e.message}")
        }
    }

    fun signOut() {
        try {
            Log.d("FirebaseAuthViewModel", "Выход из аккаунта Firebase")
            auth.signOut()
            Log.d("FirebaseAuthViewModel", "Выход из аккаунта Firebase выполнен успешно")
        } catch (e: Exception) {
            Log.e("FirebaseAuthViewModel", "Ошибка при выходе из аккаунта Firebase", e)
        }
    }

    private suspend fun saveUserInFirebase(
        token: String,
        user: User
    ) {
        try {
            Log.d("FirebaseAuthViewModel", "Сохранение данных пользователя в Firebase: ${user.username}")
            
            // Сохраняем токен авторизации
            tokenManager.saveAuthData(token = token, userId = user.userId)
            Log.d("FirebaseAuthViewModel", "Токен авторизации сохранен")

            // Сохраняем данные пользователя
            repository.setUser(
                userId = user.userId,
                username = user.username,
                avatarUrl = user.avatarUrl,
                friends = user.friends,
                location = user.location
            )
            Log.d("FirebaseAuthViewModel", "Данные пользователя сохранены в Firebase")
        } catch (e: Exception) {
            Log.e("FirebaseAuthViewModel", "Ошибка при сохранении данных пользователя", e)
            throw e
        }
    }
}
