package com.example.mapsfriends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.vk.id.AccessToken
import com.vk.id.VKIDUser
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthViewModel(private val tokenManager: AuthTokenManager) : ViewModel() {
    private val repository = FirebaseUserRepository()

    private fun saveUserToFirebase(
        user: VKIDUser,
        userId: String
    ) {
        viewModelScope.launch {
            try {
                val friends = fetchVkFriendsIds(tokenManager.getAccessToken() ?: "")

                repository.setUser(
                    userId = userId,
                    username = user.firstName ?: "User_${userId.take(4)}",
                    avatarUrl = user.photo50 ?: "",
                    friends = friends,
                    location = GeoPoint(0.0, 0.0)
                )
            } catch (e: Exception) {
                Log.e("FIREBASE", "Error saving user: ${e.stackTraceToString()}")
                // Откатываем сохранение токена при ошибке
                tokenManager.clear()
            }
        }
    }

    private suspend fun fetchVkFriendsIds(token: String): List<String> {
        return try {
            val response = withContext(Dispatchers.IO) {
                URL("https://api.vk.com/method/friends.get?access_token=$token&v=5.131")
                    .openStream().bufferedReader().use { it.readText() }
            }

            JSONObject(response)
                .getJSONObject("response")
                .getJSONArray("items")
                .let { array ->
                    List(array.length()) { index ->
                        array.getInt(index).toString()
                    }
                }
        } catch (e: Exception) {
            Log.e("VK_API", "Friends fetch error: ${e.message}")
            emptyList()
        }
    }

    fun checkAuthState(): Boolean {
        return tokenManager.getAccessToken() != null
    }
    fun login(accessToken: AccessToken) {
        // Сохраняем токен
        tokenManager.saveAuthData(
            token = accessToken.token,
            userId = accessToken.userID
        )

        // Получаем данные пользователя из токена
        val user = accessToken.userData
        val userId = accessToken.userID.toString()

        if (user != null) {
            saveUserToFirebase(user, userId)
        } else {
            Log.e("AUTH", "User data is null in access token")
            // Можно добавить повторный запрос через VKID.instance.getUserData()
        }
    }
    fun logout() {
        tokenManager.clear()
    }
}
