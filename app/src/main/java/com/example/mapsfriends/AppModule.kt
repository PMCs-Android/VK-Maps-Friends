package com.example.mapsfriends

import android.app.Application
import android.content.Context
import com.example.mapsfriends.login.AuthTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class MyApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserProfileRepository(
        eventRepositoryProvider: javax.inject.Provider<EventRepository>
    ): UserProfileRepository {
        return FirebaseUserProfileRepository(eventRepositoryProvider)
    }

    @Provides
    @Singleton
    fun provideUserFriendsRepository(
        userProfileRepository: UserProfileRepository
    ): UserFriendsRepository {
        return FirebaseUserFriendsRepository(userProfileRepository)
    }

    @Provides
    @Singleton
    fun provideEventRepository(userRepository: UserProfileRepository): EventRepository {
        return FirebaseEventRepository(userRepository)
    }

    @Provides
    @Singleton
    fun provideAuthTokenManager(@ApplicationContext context: Context): AuthTokenManager {
        return AuthTokenManager(context)
    }
}
