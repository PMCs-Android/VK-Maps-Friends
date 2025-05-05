package com.example.mapsfriends

import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.getStringList(field: String): List< String> {
    val rawList = this.get(field)
    return if (rawList is List<*>) {
        rawList.filterIsInstance<String>()
    } else {
        emptyList()
    }
}
