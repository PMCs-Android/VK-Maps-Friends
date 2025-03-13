package com.example.vkid

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.VKIDUser
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
fun ScreenVKIDButton() {
    val context = LocalContext.current
    var userInfo by remember { mutableStateOf<Map<String, String>?>(null) }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .background(gradientBrush),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userInfo == null) {
            Text(
                text = "Регистрация и вход",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            OneTap(
                onAuth = { oAuth, accessToken ->
                    getUserInfo(accessToken.token) { info ->
                        userInfo = info
                    }
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
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                scenario = OneTapTitleScenario.SignIn,
                signInAnotherAccountButtonEnabled = true,
                style = OneTapStyle
                    .Light(
                        cornersStyle = OneTapButtonCornersStyle.Custom(2f),
                        sizeStyle = OneTapButtonSizeStyle.SMALL_32,
                        elevationStyle = OneTapButtonElevationStyle.Custom(4f)
                    ),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            UserProfileScreen(userInfo!!)
        }
    }
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
                    override fun onFail(fail: VKIDGetUserFail) {
                        when (fail) {
                            is VKIDGetUserFail.FailedApiCall -> fail.description
                            is VKIDGetUserFail.IdTokenTokenExpired -> fail.description
                            is VKIDGetUserFail.NotAuthenticated -> fail.description
                        }
                    }
                }
            )
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
