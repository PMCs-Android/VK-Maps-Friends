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
fun parseEventDate(input: String): Map<String,String> {
    val (datePart, timePart) = input.split(" ")
    val (day, month) = datePart.split(".")
    return mapOf("day" to day ,
        "month" to month,
        "time" to timePart)
}