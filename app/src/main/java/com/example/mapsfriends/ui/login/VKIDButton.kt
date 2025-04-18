package com.example.mapsfriends.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mapsfriends.FirebaseUserRepository
import com.google.firebase.firestore.GeoPoint
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
fun VKIDButton() {
    val context = LocalContext.current

    OneTap(
        onAuth = { _, accessToken ->
            signUpWithVKID(
                userId = accessToken.userID.toString(),
                token = accessToken.token
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
    token: String
) {
    CoroutineScope(Dispatchers.Main).launch {
        VKID.instance.getUserData(
            callback = object : VKIDGetUserCallback {
                override fun onSuccess(user: VKIDUser) {
                    val username = user.firstName ?: "Unknown"
                    val avatarUrl = user.photo200 ?: user.photo100 ?: user.photo50 ?: ""

                    val repository = FirebaseUserRepository()

                    CoroutineScope(Dispatchers.IO).launch {
                        val friends = fetchVkFriendsIds(token)
                        repository.setUser(
                            userId = userId,
                            username = username,
                            avatarUrl = avatarUrl,
                            friends = friends,
                            location = GeoPoint(0.0, 0.0)
                        )
                    }
                }

                override fun onFail(fail: VKIDGetUserFail) {
                    when (fail) {
                        is VKIDGetUserFail.FailedApiCall -> fail.description
                        is VKIDGetUserFail.IdTokenTokenExpired -> fail.description
                        is VKIDGetUserFail.NotAuthenticated -> fail.description
                    }
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
