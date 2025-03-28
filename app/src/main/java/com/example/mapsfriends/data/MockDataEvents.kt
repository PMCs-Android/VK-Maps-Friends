package com.example.mapsfriends.data

val dateData = listOf(24, 25, 26, 27, 28, 1, 2)
val weekDays = listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс")
val monthList = listOf("янв", "фев", "март", "апр", "май", "июн", "июл", "авг", "сен", "окт", "нояб", "дек")

data class MockDataEvents(
    val name: String,
    val day: Int,
    val month: Int,
    val time: String,
    val description: String,
    val location: String,
    val members: List<MockDataUsers>
)

val mockEvents = listOf(
    MockDataEvents(
        "Баскетбол",
        27,
        2,
        "18:40",
        "Описание",
        "Новочеркасская, 9",
        listOf(mockUsers[0], mockUsers[1])
    )
)

data class Messages(
    val user: MockDataUsers,
    val text: String,
    val time: String
)

val mockMessages = listOf(
    Messages(mockUsers[1],"Возьмите воды", "18:36"),
    Messages(mockUsers[0],"Я задержусь ;,,(", "18:41")
)
