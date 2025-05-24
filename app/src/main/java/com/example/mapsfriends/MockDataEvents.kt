package com.example.mapsfriends

val monthList = listOf(
    "янв", "фев", "март", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "нояб", "дек"
)

val fullMonthList = listOf(
    "январь", "февраль", "март", "апрель", "май", "июнь",
    "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
)

data class MockDataEvents(
    val id: Int,
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
        1,
        "Баскетбол",
        27,
        2,
        "18:40",
        "бла бла бла",
        "Новочеркасская, 9",
        listOf(mockUsers[0], mockUsers[1])
    ),
    MockDataEvents(
        2,
        "Шашлычки",
        Dimensions.SEVENTEEN,
        Dimensions.SIZE_SMALL,
        "12:00",
        "шашлычок у прудика",
        "Новочеркасская, 9",
        listOf(mockUsers[2], mockUsers[Dimensions.THREE], mockUsers[0])
    ),
    MockDataEvents(
        Dimensions.THREE,
        "Аквапарк",
        25,
        Dimensions.SIZE_SMALL,
        "13:30",
        "бульк бульк",
        "Новочеркасская, 9",
        listOf(mockUsers[0], mockUsers[Dimensions.THREE])
    )
)

data class Messages(
    val user: MockDataUsers,
    val text: String,
    val time: String
)

val mockMessages = listOf(
    Messages(mockUsers[1], "Возьмите воды", "18:36"),
    Messages(mockUsers[0], "Я задержусь ;,,(", "18:41")
)
