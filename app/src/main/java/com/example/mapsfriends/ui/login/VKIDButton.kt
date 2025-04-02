package com.example.mapsfriends.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

@Composable
fun VKIDButton() {
    val context = LocalContext.current
    var userInfo by remember { mutableStateOf<Map<String, String>?>(null) }

    OneTap(
        onAuth = { oAuth, accessToken ->
            getUserInfo(accessToken.token) { info ->
                userInfo = info
            }
            Log.d("MyLog", "Sign in with VKID")
        },
        onFail = { oAuth, fail ->
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
<<<<<<< HEAD
                        Toast.LENGTH_LONG,
=======
                        Toast.LENGTH_LONG
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                    ).show()
                }
            }
        },
        scenario = OneTapTitleScenario.SignIn,
        signInAnotherAccountButtonEnabled = true,
<<<<<<< HEAD
        style =
            OneTapStyle
                .Light(
                    cornersStyle = OneTapButtonCornersStyle.Custom(2f),
                    sizeStyle = OneTapButtonSizeStyle.SMALL_32,
                    elevationStyle = OneTapButtonElevationStyle.Custom(4f),
                ),
        authParams =
            VKIDAuthUiParams {
                scopes = setOf("friends")
            },
        modifier = Modifier.padding(16.dp),
    )
}

fun getUserInfo(
    token: String,
    onResult: (Map<String, String>) -> Unit,
) {
    CoroutineScope(Dispatchers.Main).launch {
        VKID.instance.getUserData(
            callback =
                object : VKIDGetUserCallback {
                    override fun onSuccess(user: VKIDUser) {
                        val userInfo =
                            mapOf(
                                "Name" to user.firstName,
                                "LastName" to user.lastName,
                            )
                        onResult(userInfo)
                    }

=======
        style = OneTapStyle
            .Light(
                cornersStyle = OneTapButtonCornersStyle.Custom(2f),
                sizeStyle = OneTapButtonSizeStyle.SMALL_32,
                elevationStyle = OneTapButtonElevationStyle.Custom(4f)
            ),
        authParams = VKIDAuthUiParams {
            scopes = setOf("friends")
        },
        modifier = Modifier.padding(16.dp)
    )
}

fun getUserInfo(token: String, onResult: (Map<String, String>) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            VKID.instance.getUserData(
                callback = object : VKIDGetUserCallback {
                    override fun onSuccess(user: VKIDUser) {
                        val userInfo = mapOf(
                            "Name" to user.firstName,
                            "LastName" to user.lastName
                        )
                        onResult(userInfo)
                    }
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                    override fun onFail(fail: VKIDGetUserFail) {
                        when (fail) {
                            is VKIDGetUserFail.FailedApiCall -> fail.description
                            is VKIDGetUserFail.IdTokenTokenExpired -> fail.description
                            is VKIDGetUserFail.NotAuthenticated -> fail.description
                        }
                    }
<<<<<<< HEAD
                },
        )
    }
}
=======
                }
            )
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
