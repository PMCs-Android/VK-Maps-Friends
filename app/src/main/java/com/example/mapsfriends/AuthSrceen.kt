package com.example.mapsfriends

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.common.button.style.OneTapButtonElevationStyle
import com.vk.id.onetap.common.button.style.OneTapButtonSizeStyle
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel()
    OneTap(
        onAuth = { _, accessToken ->
            viewModel.login(accessToken)
            onLoginSuccess()
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
