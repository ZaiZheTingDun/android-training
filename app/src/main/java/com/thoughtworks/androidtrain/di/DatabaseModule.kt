package com.thoughtworks.androidtrain.di

import android.content.Context
import androidx.room.Room
import com.thoughtworks.androidtrain.data.source.ITweetRepository
import com.thoughtworks.androidtrain.data.source.TweetRepository
import com.thoughtworks.androidtrain.data.source.local.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "practice-db").build()

    @Provides
    fun provideSenderDao(appDatabase: AppDatabase) =
        appDatabase.senderDao()

    @Provides
    fun provideImageDao(appDatabase: AppDatabase) =
        appDatabase.imageDao()

    @Provides
    fun provideCommentDao(appDatabase: AppDatabase) =
        appDatabase.commentDao()

    @Provides
    fun provideTweetDao(appDatabase: AppDatabase) =
        appDatabase.tweetDao()

    @Provides
    fun provideTweetRepository(tweetRepository: TweetRepository): ITweetRepository = tweetRepository
}