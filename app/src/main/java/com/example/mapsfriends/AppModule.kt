package com.example.mapsfriends

import android.content.Context
import com.example.mapsfriends.login.AuthTokenManager
import com.example.mapsfriends.user.UserManager
import com.example.mapsfriends.FirebaseUserRepository
import com.example.mapsfriends.FirebaseEventRepository
import com.example.mapsfriends.UserRepository
import com.example.mapsfriends.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthTokenManager(
        @ApplicationContext context: Context
    ): AuthTokenManager {
        return AuthTokenManager(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return FirebaseUserRepository()
    }

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository {
        return FirebaseEventRepository()
    }

    @Provides
    @Singleton
    fun provideUserManager(
        @ApplicationContext context: Context,
        userRepository: UserRepository
    ): UserManager {
        return UserManager(context, userRepository)
    }
}
