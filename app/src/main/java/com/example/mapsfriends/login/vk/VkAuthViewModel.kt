package com.example.mapsfriends.login.vk

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mapsfriends.FirebaseUserRepository
import com.example.mapsfriends.User
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.GeoPoint
import com.vk.id.VKID
import com.vk.id.VKIDUser
import com.vk.id.refreshuser.VKIDGetUserCallback
import com.vk.id.refreshuser.VKIDGetUserFail
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import org.json.JSONException
import org.json.JSONObject

@HiltViewModel
class VkAuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val repository = FirebaseUserRepository()

    fun signUp(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            VKID.instance.getUserData(
                callback = object : VKIDGetUserCallback {
                    override fun onSuccess(user: VKIDUser) {
                        val token = tokenManager.getAccessToken()

                        if (token == null) {
                            Log.d("AUTH", "The token has not been saved")
                            return
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            saveUserInFirebase(
                                token = token!!,
                                User(
                                    userId = tokenManager.getUserId()!!,
                                    username = user.firstName + " " + user.lastName,
                                    avatarUrl = user.photo200 ?: "",
                                    friends = fetchVkFriendsIds(token),
                                    location = GeoPoint(0.0, 0.0)
                                )
                            )

                            withContext(Dispatchers.Main) {
                                onSuccess()
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

    private suspend fun fetchVkFriendsIds(token: String): List<String> {
        val url = "https://api.vk.com/method/friends.get?access_token=$token&v=5.131"
        var connection: HttpURLConnection? = null

        return try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream.bufferedReader().use { it.readText() }

            val json = JSONObject(inputStream)
            // Проверяем наличие поля "response" в ответе
            if (!json.has("response")) {
                throw JSONException("Invalid response format: no 'response' field")
            }

            val items = json.getJSONObject("response").getJSONArray("items")

            val friendIds = mutableListOf<String>()
            for (i in 0 until items.length()) {
                friendIds.add(items.getInt(i).toString())
            }
            friendIds
        } catch (e: IOException) {
            Log.e("VK_API", "Network error fetching friends: ${e.message}", e)
            emptyList()
        } catch (e: JSONException) {
            Log.e("VK_API", "JSON parsing error: ${e.message}", e)
            emptyList()
        } finally {
            connection?.disconnect()
        }
    }

    private suspend fun saveUserInFirebase(
        token: String,
        user: User
    ) {
        tokenManager.saveAuthData(token = token, userId = user.userId)

        repository.setUser(
            userId = user.userId,
            username = user.username,
            avatarUrl = user.avatarUrl,
            friends = user.friends,
            location = user.location
        )
    }
}
