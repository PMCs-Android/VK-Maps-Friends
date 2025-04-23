package com.example.mapsfriends.user

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mapsfriends.User
import com.example.mapsfriends.UserRepository
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) {
    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
    }

    // Локальное хранение основных данных пользователя
    suspend fun saveUserLocally(user: User) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = user.userId
            preferences[PreferencesKeys.USER_NAME] = user.username
            preferences[PreferencesKeys.USER_AVATAR] = user.avatarUrl
        }
    }

    // Получение данных пользователя из локального хранилища
    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val userId = preferences[PreferencesKeys.USER_ID] ?: return@map null
        val userName = preferences[PreferencesKeys.USER_NAME] ?: return@map null
        val userAvatar = preferences[PreferencesKeys.USER_AVATAR] ?: return@map null
        
        Log.d("UserManager", "Инициализация пользователя: id=$userId, name=$userName")
        
        User(
            userId = userId,
            username = userName,
            avatarUrl = userAvatar,
            friends = emptyList(), // Будет обновлено из Firebase
            location = GeoPoint(0.0, 0.0) // Будет обновлено из Firebase
        )
    }

    // Синхронизация данных пользователя с Firebase
    suspend fun syncUserData(userId: String) {
        Log.d("UserManager", "Синхронизация данных пользователя: $userId")
        val firebaseUser = userRepository.getUserById(userId)
        firebaseUser?.let { user ->
            Log.d("UserManager", "Получены данные из Firebase: ${user.username}")
            saveUserLocally(user)
        } ?: Log.e("UserManager", "Не удалось получить данные пользователя из Firebase")
    }

    // Обновление локации пользователя
    suspend fun updateUserLocation(location: GeoPoint) {
        context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }.collect { userId ->
            userId?.let {
                userRepository.updateUserLocation(it, location)
            }
        }
    }

    // Получение друзей пользователя
    suspend fun getUserFriends(userId: String): List<User>? {
        return userRepository.getFriendsList(userId)
    }

    // Наблюдение за друзьями пользователя
    suspend fun observeFriends(userId: String, callback: (List<User>) -> Unit) {
        userRepository.observeFriendsList(userId, callback)
    }

    // Функция для заполнения тестовых данных пользователя
    suspend fun fillTestUserData() {
        val testUser = User(
            userId = "test_user_id",
            username = "Тестовый Пользователь",
            avatarUrl = "https://example.com/avatar.jpg",
            friends = listOf("friend1", "friend2"),
            location = GeoPoint(55.7558, 37.6173) // Москва
        )
        
        Log.d("UserManager", "Заполнение тестовых данных пользователя: ${testUser.username}")
        
        // Сохраняем локально
        saveUserLocally(testUser)
        
        // Сохраняем в Firebase
        userRepository.setUser(
            userId = testUser.userId,
            username = testUser.username,
            avatarUrl = testUser.avatarUrl,
            friends = testUser.friends,
            location = testUser.location
        )
    }
    
    // Функция для выхода из аккаунта
    suspend fun logout() {
        Log.d("UserManager", "Выход из аккаунта")
        try {
            // Очищаем локальные данные
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
            Log.d("UserManager", "Локальные данные пользователя очищены")
        } catch (e: Exception) {
            Log.e("UserManager", "Ошибка при выходе из аккаунта", e)
            // Продолжаем выполнение, даже если очистка данных не удалась
        }
    }
} 