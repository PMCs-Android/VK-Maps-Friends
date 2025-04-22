package com.example.mapsfriends.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mapsfriends.login.AuthViewModel
import com.example.mapsfriends.login.vk.VkAuthViewModel
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.common.button.style.OneTapButtonElevationStyle
import com.vk.id.onetap.common.button.style.OneTapButtonSizeStyle
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario

@Composable
fun VKIDButton(
    onLoginSuccess: () -> Unit,
    tokenManager: AuthViewModel = hiltViewModel(),
    vkAuthViewModel: VkAuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    OneTap(
        onAuth = { _, accessToken ->
            tokenManager.saveAuthData(
                token = accessToken.token,
                userId = accessToken.userID.toString()
            )

            vkAuthViewModel.signUp(onSuccess = onLoginSuccess)
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
