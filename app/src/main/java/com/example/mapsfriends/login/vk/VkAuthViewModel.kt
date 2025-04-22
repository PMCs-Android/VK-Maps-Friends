package com.example.mapsfriends.login.vk

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mapsfriends.FirebaseUserRepository
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.GeoPoint
import com.vk.id.VKID
import com.vk.id.VKIDUser
import com.vk.id.refreshuser.VKIDGetUserCallback
import com.vk.id.refreshuser.VKIDGetUserFail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VkAuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val repository = FirebaseUserRepository()
    private val token = tokenManager.getAccessToken()

    fun signUp(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            VKID.instance.getUserData(
                callback = object : VKIDGetUserCallback {
                    override fun onSuccess(user: VKIDUser) {
                        if (tokenManager.getAccessToken() == null) {
                            Log.d("AUTH", "The token has not been saved")
                            return
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                saveUserInFirebase(
                                    token = token!!,
                                    userId = tokenManager.getUserId()!!,
                                    username = user.firstName + " " + user.lastName,
                                    avatarUrl = user.photo200 ?: "",
                                    friends = fetchVkFriendsIds(),
                                    location = GeoPoint(0.0, 0.0)
                                )

                                withContext(Dispatchers.Main) {
                                    onSuccess()
                                }
                            } catch (e: Exception) {
                                tokenManager.clear()
                            }
                        }
                    }

                    override fun onFail(fail: VKIDGetUserFail) {
                        tokenManager.clear()
                    }
                }
            )
        }
    }

    private suspend fun fetchVkFriendsIds(): List<String> {
        val url =
            "https://api.vk.com/method/friends.get?access_token=$token&v=5.131"

        return try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }

            val json = org.json.JSONObject(inputStream)
            val items = json.getJSONObject("response").getJSONArray("items")

            val friendIds = mutableListOf<String>()
            for (i in 0 until items.length()) {
                friendIds.add(items.getInt(i).toString())
            }
            friendIds
        } catch (e: Exception) {
            Log.e("VK_API", "Friends fetch error: ${e.message}")
            emptyList()
        }
    }

    private suspend fun saveUserInFirebase(
        token: String,
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    ) {
        tokenManager.saveAuthData(token = token, userId = userId)

        repository.setUser(
            userId = userId,
            username = username,
            avatarUrl = avatarUrl,
            friends = friends,
            location = location
        )
    }

}