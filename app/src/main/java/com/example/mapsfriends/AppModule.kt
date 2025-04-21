package com.example.mapsfriends

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class MyApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        eventRepositoryProvider: javax.inject.Provider<EventRepository>
    ): UserRepository {
        return FirebaseUserRepository(eventRepositoryProvider)
    }

    @Provides
    @Singleton
    fun provideEventRepository(userRepository: UserRepository): EventRepository {
        return FirebaseEventRepository(userRepository)
    }
}
