package com.example.mapsfriends

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class  MyApp: Application()

@Module
@InstallIn (SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository{
        return FirebaseUserRepository()
    }

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository{
        return FirebaseEventRepository()
    }
}
