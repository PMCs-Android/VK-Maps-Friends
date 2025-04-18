package com.example.mapsfriends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.VKID
import com.vk.id.VKIDUser
import com.vk.id.refreshuser.VKIDGetUserCallback
import com.vk.id.refreshuser.VKIDGetUserFail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    fun getUserData(token: String, onSuccess: (VKIDUser) -> Unit) {
        viewModelScope.launch {
            VKID.instance.getUserData(
                callback = object : VKIDGetUserCallback {
                    override fun onSuccess(user: VKIDUser) {
                        onSuccess(user)
                    }
                    override fun onFail(fail: VKIDGetUserFail) {
                        Log.e("VK_USER", "Error: ${fail.description}")
                    }
                }
            )
        }
    }

    fun saveUserToFirebase(user: VKIDUser){
        viewModelScope.launch {
            try {
                //FirebaseUserRepository().setUser(
                    //userId = user.id.toString(),
                    //username = TODO(),
                    //avatarUrl = TODO(),
                    //friends = TODO(),
                    //location = TODO(),
                //)
                Log.d("FIREBASE", "User saved!")
            } catch (e: Exception) {
                Log.e("FIREBASE", "Save error: ${e.message}")
            }
        }
    }
}