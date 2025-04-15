package com.example.mapsfriends

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

data class MockDataUsers(
    val id: Int,
    val name: String,
    val avatarUrl: String,
    val location: LatLng
)

// Моковые данные пользователей
val mockUsers = listOf(
    MockDataUsers(
        id = 1,
        name = "Алиса",
        avatarUrl = (
            "https://steamuserimages-a.akamaihd.net/ugc/2100422066956953334/" +
                "BCFFD0DB0C56F71CD288304540E39FC2FADFD155/?imw=512&imh=341&ima=fit&impolicy" +
                "=Letterbox&imcolor=%23000000&letterbox=true"
            ),
        location = LatLng(55.751244, 37.618423)
    ),
    MockDataUsers(
        id = 2,
        name = "Боб",
        avatarUrl = (
            "https://avatars.mds.yandex.net/get-mpic/5346238/img_id1357746595382532818.jpeg/orig"
            ),
        location = LatLng(55.740000, 37.600000)
    ),
    MockDataUsers(
        id = 3,
        name = "Каролина",
        avatarUrl = (
            "https://avatars.mds.yandex.net/i?id=3ec440083d4334d" +
                "a05c99cc318e08a51_l-9065817-images-thumbs&n=13"
            ),
        location = LatLng(55.760000, 37.620000)
    )
)

val currentUser = User(
    userId = "111",
    username = "test user",
    avatarUrl = "https://avatars.mds.yandex.net/get-mpic/5346238/img_id1357746595382532818." +
        "jpeg/orig",
    friends = listOf("112", "113"),
    location = GeoPoint(55.751244, 37.618423)
)

val friend1 = User(
    userId = "112",
    username = "test user2",
    avatarUrl = "https://avatars.mds.yandex.net/get-entity_search/1969011/918366713/orig",
    friends = listOf(),
    location = GeoPoint(55.740000, 37.600000)
)

val friend2 = User(
    userId = "113",
    username = "test user3",
    avatarUrl = "https://i.pinimg.com/736x/be/46/46/be4646a42ab267d93f035f93752eb796.jpg",
    friends = listOf(),
    location = GeoPoint(55.760000, 37.620000)
)
