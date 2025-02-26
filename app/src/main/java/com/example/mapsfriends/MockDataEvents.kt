package com.example.mapsfriends

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