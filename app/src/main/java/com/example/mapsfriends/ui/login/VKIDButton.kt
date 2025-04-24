package com.example.mapsfriends.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mapsfriends.login.AuthViewModel
import com.example.mapsfriends.login.vk.AuthState
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
    val authState by vkAuthViewModel.authState.collectAsState()

    // Обработка состояния авторизации
    LaunchedEffect(authState) {
        try {
            when (authState) {
                is AuthState.Success -> {
                    Log.d("VKIDButton", "Успешная авторизация через VK")
                    val user = (authState as AuthState.Success).user
                    // Сохраняем токен и ID пользователя
                    tokenManager.saveAuthData(
                        token = user.userId, // Используем VK ID как токен
                        userId = user.userId
                    )
                    onLoginSuccess()
                }
                is AuthState.Error -> {
                    val errorMessage = (authState as AuthState.Error).message
                    Log.e("VKIDButton", "Ошибка авторизации через VK: $errorMessage")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {
                    Log.d("VKIDButton", "Состояние авторизации: $authState")
                }
            }
        } catch (e: Exception) {
            Log.e("VKIDButton", "Ошибка при обработке состояния авторизации", e)
            Toast.makeText(context, "Ошибка при авторизации: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    OneTap(
        onAuth = { _, accessToken ->
            try {
                Log.d("VKIDButton", "Получен токен VK: ${accessToken.token}")
                // Передаем токен в метод signUp для авторизации
                vkAuthViewModel.signUp(vkToken = accessToken.token)
            } catch (e: Exception) {
                Log.e("VKIDButton", "Ошибка при авторизации через VK", e)
                Toast.makeText(context, "Ошибка при авторизации: ${e.message}", Toast.LENGTH_LONG).show()
            }
        },
        onFail = { _, fail ->
            try {
                Log.e("VKIDButton", "Ошибка авторизации VK: ${fail.description}")
                when (fail) {
                    is VKIDAuthFail.Canceled -> {
                        Toast.makeText(context, "Авторизация отменена", Toast.LENGTH_LONG).show()
                    }
                    is VKIDAuthFail.FailedApiCall -> {
                        Toast.makeText(context, "Ошибка API VK: ${fail.description}", Toast.LENGTH_LONG).show()
                    }
                    is VKIDAuthFail.FailedOAuthState -> {
                        Toast.makeText(context, "Ошибка состояния OAuth: ${fail.description}", Toast.LENGTH_LONG).show()
                    }
                    is VKIDAuthFail.FailedRedirectActivity -> {
                        Toast.makeText(context, "Ошибка перенаправления: ${fail.description}", Toast.LENGTH_LONG).show()
                    }
                    is VKIDAuthFail.NoBrowserAvailable -> {
                        Toast.makeText(context, "Браузер недоступен", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Ошибка авторизации: ${fail.description}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("VKIDButton", "Ошибка при обработке ошибки авторизации", e)
                Toast.makeText(context, "Неизвестная ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        },


    )
}


