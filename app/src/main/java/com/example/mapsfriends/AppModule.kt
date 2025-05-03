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
    fun provideUserProfileRepository(): UserProfileRepository {
        return FirebaseUserProfileRepository()
    }

    @Provides
    @Singleton
    fun provideUserFriendsRepository(): UserFriendsRepository {
        return FirebaseUserFriendsRepository()
    }

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository {
        return FirebaseEventRepository()
    }

    @Provides
    @Singleton
    fun provideAuthTokenManager(@ApplicationContext context: Context): AuthTokenManager {
        return AuthTokenManager(context)
    }
}
