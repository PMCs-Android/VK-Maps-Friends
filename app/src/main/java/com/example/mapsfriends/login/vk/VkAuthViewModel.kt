package com.example.mapsfriends.login.vk

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class VkAuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(vkToken: String) {
        viewModelScope.launch {
            try {
                Log.d("VkAuthViewModel", "Начало процесса регистрации через VK")
                _authState.value = AuthState.Loading

                // Получаем данные пользователя из ВК
                val userData = try {
                    fetchVkUserData(vkToken)
                } catch (e: Exception) {
                    Log.e("VkAuthViewModel", "Ошибка при получении данных пользователя из VK", e)
                    _authState.value = AuthState.Error("Ошибка при получении данных пользователя из VK: ${e.message}")
                    return@launch
                }
                
                // Проверяем наличие необходимых полей
                if (!userData.has("first_name") || !userData.has("last_name")) {
                    Log.e("VkAuthViewModel", "Отсутствуют обязательные поля в данных пользователя")
                    _authState.value = AuthState.Error("Отсутствуют обязательные поля в данных пользователя")
                    return@launch
                }

                val firstName = userData.getString("first_name")
                val lastName = userData.getString("last_name")
                val photoUrl = userData.optString("photo_max", "")
                
                Log.d("VkAuthViewModel", "Получены данные пользователя из VK: firstName=$firstName, lastName=$lastName, photoUrl=$photoUrl")

                // Проверяем, авторизован ли пользователь в Firebase
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    Log.e("VkAuthViewModel", "Пользователь не авторизован в Firebase")
                    _authState.value = AuthState.Error("Пользователь не авторизован в Firebase")
                    return@launch
                }

                // Создаем пользователя в Firebase
                val user = User(
                    userId = currentUser.uid,
                    username = "$firstName $lastName",
                    avatarUrl = photoUrl,
                    friends = emptyList()
                )
                Log.d("VkAuthViewModel", "Создан объект пользователя: ${user.username}")

                // Сохраняем пользователя в Firestore
                try {
                    firestore.collection("users")
                        .document(user.userId)
                        .set(user)
                        .await()
                    Log.d("VkAuthViewModel", "Пользователь успешно сохранен в Firestore")
                } catch (e: Exception) {
                    Log.e("VkAuthViewModel", "Ошибка при сохранении пользователя в Firestore", e)
                    _authState.value = AuthState.Error("Ошибка при сохранении пользователя в Firestore: ${e.message}")
                    return@launch
                }

                _authState.value = AuthState.Success(user)
                Log.d("VkAuthViewModel", "Регистрация через VK успешно завершена")
            } catch (e: Exception) {
                Log.e("VkAuthViewModel", "Ошибка при регистрации через VK", e)
                _authState.value = AuthState.Error("Ошибка при регистрации через VK: ${e.message}")
            }
        }
    }

    private suspend fun fetchVkUserData(token: String): JSONObject {
        try {
            Log.d("VkAuthViewModel", "Запрос данных пользователя из VK API")
            val url = URL("https://api.vk.com/method/users.get?fields=photo_max&access_token=$token&v=5.131")
            val response = url.readText()
            Log.d("VkAuthViewModel", "Получен ответ от VK API: $response")
            
            val json = JSONObject(response)
            
            // Проверяем наличие ошибки в ответе
            if (json.has("error")) {
                val error = json.getJSONObject("error")
                val errorMsg = error.optString("error_msg", "Unknown VK API error")
                Log.e("VkAuthViewModel", "Ошибка VK API: $errorMsg")
                throw Exception("Ошибка VK API: $errorMsg")
            }
            
            if (!json.has("response")) {
                Log.e("VkAuthViewModel", "Отсутствует поле response в ответе VK API")
                throw Exception("Отсутствует поле response в ответе VK API")
            }
            
            val responseObj = json.getJSONArray("response")
            if (responseObj.length() == 0) {
                Log.e("VkAuthViewModel", "Пустой ответ от VK API")
                throw Exception("Пустой ответ от VK API")
            }
            
            val userData = responseObj.getJSONObject(0)
            Log.d("VkAuthViewModel", "Успешно получены данные пользователя: $userData")
            return userData
        } catch (e: Exception) {
            Log.e("VkAuthViewModel", "Ошибка при запросе данных пользователя из VK API", e)
            throw e
        }
    }

    private suspend fun fetchVkFriendsIds(token: String): List<String> {
        try {
            Log.d("VkAuthViewModel", "Запрос списка друзей из VK API")
            val url = URL("https://api.vk.com/method/friends.get?access_token=$token&v=5.131")
            val response = url.readText()
            Log.d("VkAuthViewModel", "Получен ответ от VK API (friends): $response")
            
            val json = JSONObject(response)
            
            // Проверяем наличие ошибки в ответе
            if (json.has("error")) {
                val error = json.getJSONObject("error")
                val errorMsg = error.optString("error_msg", "Unknown VK API error")
                Log.e("VkAuthViewModel", "Ошибка VK API: $errorMsg")
                throw Exception("Ошибка VK API: $errorMsg")
            }
            
            if (!json.has("response")) {
                Log.e("VkAuthViewModel", "Отсутствует поле response в ответе VK API (friends)")
                throw Exception("Отсутствует поле response в ответе VK API (friends)")
            }
            
            val responseObj = json.getJSONObject("response")
            if (!responseObj.has("items")) {
                Log.e("VkAuthViewModel", "Отсутствует поле items в ответе VK API (friends)")
                throw Exception("Отсутствует поле items в ответе VK API (friends)")
            }
            
            val items = responseObj.getJSONArray("items")
            
            val friendsIds = mutableListOf<String>()
            for (i in 0 until items.length()) {
                friendsIds.add(items.getString(i))
            }
            
            Log.d("VkAuthViewModel", "Получено ${friendsIds.size} друзей из VK API")
            return friendsIds
        } catch (e: Exception) {
            Log.e("VkAuthViewModel", "Ошибка при запросе списка друзей из VK API", e)
            throw e
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
