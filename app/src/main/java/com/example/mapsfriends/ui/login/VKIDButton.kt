package com.example.mapsfriends.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mapsfriends.AuthTokenManager
import com.example.mapsfriends.FirebaseUserRepository
import com.google.firebase.firestore.GeoPoint
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.VKIDUser
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.common.button.style.OneTapButtonElevationStyle
import com.vk.id.onetap.common.button.style.OneTapButtonSizeStyle
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario
import com.vk.id.refreshuser.VKIDGetUserCallback
import com.vk.id.refreshuser.VKIDGetUserFail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VKIDButton(
    tokenManager: AuthTokenManager, // Принимаем менеджер как параметр
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    OneTap(
        onAuth = { _, accessToken ->
            tokenManager.saveAuthData(
                token = accessToken.token,
                userId = accessToken.userID.toString()
            )

            signUpWithVKID(
                userId = accessToken.userID.toString(),
                token = accessToken.token,
                tokenManager = tokenManager, // Передаем дальше
                onSuccess = onLoginSuccess
            )
        },
        onFail = { _, fail ->
            when (fail) {
                is VKIDAuthFail.Canceled -> {
                    Toast.makeText(context, fail.description, Toast.LENGTH_LONG).show()
                }
                is VKIDAuthFail.FailedApiCall -> {
                    Toast.makeText(context, fail.description, Toast.LENGTH_LONG).show()
                }
                is VKIDAuthFail.FailedOAuthState -> {
                    Toast.makeText(context, fail.description, Toast.LENGTH_LONG).show()
                }
                is VKIDAuthFail.FailedRedirectActivity -> {
                    Toast.makeText(context, fail.description, Toast.LENGTH_LONG).show()
                }
                is VKIDAuthFail.NoBrowserAvailable -> {
                    Toast.makeText(context, fail.description, Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(
                        context,
                        fail.description,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        },
        scenario = OneTapTitleScenario.SignIn,
        signInAnotherAccountButtonEnabled = true,
        style =
            OneTapStyle
                .Light(
                    cornersStyle = OneTapButtonCornersStyle.Custom(2f),
                    sizeStyle = OneTapButtonSizeStyle.SMALL_32,
                    elevationStyle = OneTapButtonElevationStyle.Custom(4f),
                ),
        authParams =
            VKIDAuthUiParams {
                scopes = setOf("email", "friends")
            },
        modifier = Modifier.padding(16.dp),
    )
}

fun signUpWithVKID(
    userId: String,
    token: String,
    tokenManager: AuthTokenManager, // Добавляем параметр
    onSuccess: () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        VKID.instance.getUserData(
            callback = object : VKIDGetUserCallback {
                override fun onSuccess(user: VKIDUser) {
                    // Проверяем, что токен сохранен
                    if (tokenManager.getAccessToken() == null) {
                        Log.e("AUTH", "Токен не сохранен!")
                        return
                    }

                    // Сохраняем пользователя в Firebase
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val friends = fetchVkFriendsIds(token)
                            FirebaseUserRepository().setUser(
                                userId = userId,
                                username = user.firstName ?: "User",
                                avatarUrl = user.photo50 ?: "",
                                friends = friends,
                                location = GeoPoint(0.0, 0.0)  // Теперь работает
                            )

                            withContext(Dispatchers.Main) {  // Теперь распознается
                                onSuccess()
                            }
                        } catch (e: Exception) {
                            tokenManager.clear()
                        }
                    }
                }

                override fun onFail(fail: VKIDGetUserFail) {
                    tokenManager.clear() // Чистим токен при ошибке
                }
            }
        )
    }
}

suspend fun fetchVkFriendsIds(accessToken: String): List<String> {
    val url =
        "https://api.vk.com/method/friends.get?access_token=$accessToken&v=5.131"

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
        Log.e("VK_API", "Ошибка получения друзей: ${e.message}")
        emptyList()
    }
}
