package com.example.mapsfriends.ui.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileScreenDataObject(
    val uid: String = "",
    val email: String = "",
)
