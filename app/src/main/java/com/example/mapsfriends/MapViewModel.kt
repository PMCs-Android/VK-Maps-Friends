package com.example.mapsfriends

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import com.example.mapsfriends.user.UserManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class MapViewModel @Inject constructor(
    val userManager: UserManager,
    private val userRepository: UserRepository,
    private val authTokenManager: AuthTokenManager
) : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<String?>(null)
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            userManager.userFlow.collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            _selectedUser.value = userRepository.getUserById(userId)
        }
    }

    fun setupMarkersAndObserveLocations(context: Context, userId: String) {
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Начало настройки маркеров для пользователя: $userId")
                
                // Загружаем данные текущего пользователя из Firebase
                val currentUserFromFirebase = userRepository.getUserById(userId)
                
                if (currentUserFromFirebase == null) {
                    Log.e("MapViewModel", "Не удалось получить данные пользователя из Firebase")
                    return@launch
                }
                
                Log.d("MapViewModel", "Получены данные пользователя: ${currentUserFromFirebase.username}")
                
                // Обновляем текущего пользователя
                _currentUser.value = currentUserFromFirebase
                
                // Добавляем маркер текущего пользователя
                addUserMarker(context, currentUserFromFirebase, true)
                
                // Наблюдаем за изменением локации текущего пользователя
                userRepository.observeLocation(userId) { newLocation ->
                    Log.d("MapViewModel", "Обновление локации: ${newLocation.latitude}, ${newLocation.longitude}")
                    updateMarkerPosition(userId, newLocation)
                }
                
                // Загружаем и наблюдаем за друзьями
                userManager.observeFriends(userId) { friends ->
                    Log.d("MapViewModel", "Получен список друзей: ${friends.size}")
                    updateMarkers(context, friends)
                }
                
                Log.d("MapViewModel", "Настройка маркеров завершена")
                
            } catch (e: Exception) {
                Log.e("MapViewModel", "Ошибка при настройке маркеров", e)
            }
        }
    }

    private fun updateMarkers(context: Context, friends: List<User>) {
        viewModelScope.launch {
            val friendIds = friends.map { it.userId }
            
            // Удаляем маркеры для пользователей, которые больше не друзья
            // Но не удаляем маркер текущего пользователя
            markers.removeAll { marker -> 
                marker.id !in friendIds && marker.id != _currentUser.value?.userId 
            }

            friends.forEach { user ->
                val userId = user.userId

                if (userId.isNullOrBlank()) {
                    Log.e("MapViewModel", "Invalid userId: $userId for user ${user.username}")
                    return@forEach
                }

                addUserMarker(context, user, false)
            }
        }
    }
    
    private fun addUserMarker(context: Context, user: User, isCurrentUser: Boolean) {
        try {
            val userId = user.userId
            if (userId.isBlank()) {
                Log.e("MapViewModel", "Некорректный userId для пользователя: ${user.username}")
                return
            }
            
            val existingIndex = markers.indexOfFirst { it.id == userId }
            
            Log.d("MapViewModel", "Добавление маркера для пользователя: ${user.username}")
            Log.d("MapViewModel", "Позиция: ${user.location.latitude}, ${user.location.longitude}")
            
            // Наблюдаем за изменением локации пользователя
            viewModelScope.launch {
                try {
                    userRepository.observeLocation(userId) { newLocation ->
                        Log.d("MapViewModel", "Обновление позиции для ${user.username}: ${newLocation.latitude}, ${newLocation.longitude}")
                        updateMarkerPosition(userId, newLocation)
                    }
                } catch (e: Exception) {
                    Log.e("MapViewModel", "Ошибка при наблюдении за локацией", e)
                }
            }
            
            // Загружаем аватар пользователя
            viewModelScope.launch {
                try {
                    val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                    Log.d("MapViewModel", "Загружен аватар для ${user.username}: ${originalBitmap != null}")
                    
                    if (originalBitmap == null) {
                        Log.e("MapViewModel", "Не удалось загрузить аватар для ${user.username}")
                        return@launch
                    }
                    
                    val initialSize = calculateMarkerSize(18f)
                    Log.d("MapViewModel", "Размер маркера: $initialSize")
                    
                    val newMarker = MarkerData(
                        id = userId,
                        position = convertToLatLng(user.location),
                        title = user.username,
                        originalBitmap = originalBitmap,
                        icon = BitmapDescriptorFactory.fromBitmap(
                            createMarkerWithBorderAndTail(context, originalBitmap, initialSize)
                        )
                    )
                    
                    withContext(Dispatchers.Main) {
                        if (existingIndex != -1) {
                            markers[existingIndex] = newMarker
                            Log.d("MapViewModel", "Обновлен существующий маркер для ${user.username}")
                        } else {
                            markers.add(newMarker)
                            Log.d("MapViewModel", "Добавлен новый маркер для ${user.username}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MapViewModel", "Ошибка при создании маркера", e)
                }
            }
        } catch (e: Exception) {
            Log.e("MapViewModel", "Ошибка в методе addUserMarker", e)
        }
    }

    fun updateMarkerIcons(currentZoom: Float, context: Context) {
        viewModelScope.launch {
            markers.forEach { marker ->
                val newSize = calculateMarkerSize(currentZoom)
                val newIcon = BitmapDescriptorFactory.fromBitmap(
                    createMarkerWithBorderAndTail(context, marker.originalBitmap, newSize)
                )
                withContext(Dispatchers.Main) {
                    marker.icon = newIcon
                }
            }
        }
    }

    private fun updateMarkerPosition(userId: String, newLocation: GeoPoint) {
        val markerIndex = markers.indexOfFirst { it.id == userId }
        if (markerIndex != -1) {
            val updatedMarker = markers[markerIndex].copy(position = convertToLatLng(newLocation))
            markers[markerIndex] = updatedMarker
        }
    }

    private fun convertToLatLng(point: GeoPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    // Публичные методы для управления пользователем
    suspend fun logout() {
        try {
            Log.d("MapViewModel", "Начало процесса выхода из аккаунта")
            
            // Очищаем текущего пользователя
            _currentUser.value = null
            _selectedUser.value = null
            
            // Очищаем маркеры
            markers.clear()
            
            // Очищаем выбранный маркер
            selectedMarkerId.value = null
            
            try {
                // Очищаем токен авторизации
                authTokenManager.clear()
                Log.d("MapViewModel", "Токен авторизации успешно очищен")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Ошибка при очистке токена авторизации", e)
                // Продолжаем выполнение, даже если очистка токена не удалась
            }
            
            try {
                // Выходим из аккаунта через UserManager
                userManager.logout()
                Log.d("MapViewModel", "Выход из аккаунта через UserManager выполнен")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Ошибка при выходе из аккаунта через UserManager", e)
                // Продолжаем выполнение, даже если выход через UserManager не удался
            }
            
            Log.d("MapViewModel", "Выход из аккаунта успешно выполнен")
        } catch (e: Exception) {
            Log.e("MapViewModel", "Ошибка при выходе из аккаунта", e)
            // Даже в случае ошибки, пытаемся очистить токен авторизации
            try {
                authTokenManager.clear()
            } catch (e2: Exception) {
                Log.e("MapViewModel", "Критическая ошибка при очистке токена", e2)
            }
        }
    }

    suspend fun fillTestData(context: Context) {
        try {
            Log.d("MapViewModel", "Начало заполнения тестовых данных")
            
            // Создаем тестового пользователя с корректными данными
            val testUser = User(
                userId = "test_user_id",
                username = "Тестовый Пользователь",
                avatarUrl = "https://example.com/avatar.jpg",
                friends = listOf("friend1", "friend2"),
                location = GeoPoint(55.7558, 37.6173) // Москва
            )
            
            Log.d("MapViewModel", "Тестовый пользователь создан: ${testUser.username}")
            
            // Сохраняем в Firebase
            userRepository.setUser(
                userId = testUser.userId,
                username = testUser.username,
                avatarUrl = testUser.avatarUrl,
                friends = testUser.friends,
                location = testUser.location
            )
            
            Log.d("MapViewModel", "Данные сохранены в Firebase")
            
            // Сохраняем локально
            userManager.saveUserLocally(testUser)
            
            Log.d("MapViewModel", "Данные сохранены локально")
            
            // Инициализируем маркеры
            setupMarkersAndObserveLocations(context, testUser.userId)
            
            Log.d("MapViewModel", "Маркеры инициализированы")
            
        } catch (e: Exception) {
            Log.e("MapViewModel", "Ошибка при заполнении тестовых данных", e)
        }
    }
}
