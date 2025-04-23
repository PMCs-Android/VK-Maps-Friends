package com.example.mapsfriends

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    // Хранение основных данных пользователя локально
    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
    }

    // Flow для получения данных пользователя
    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val userId = preferences[PreferencesKeys.USER_ID] ?: return@map null
        User(/* ... */)
    }

    // Методы для работы с данными
    suspend fun syncUserData(userId: String){}
    suspend fun updateUserLocation(location: GeoPoint){}
    //fun getUserFriends(): Flow<List<User>>{}
}