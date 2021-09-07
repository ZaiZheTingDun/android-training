package com.thoughtworks.androidtrain.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thoughtworks.androidtrain.data.source.local.room.dao.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.*

@Database(
    entities = [
        TweetEntity::class,
        ImageEntity::class,
        SenderEntity::class,
        UserEntity::class,
        CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tweetDao(): TweetDao
    abstract fun senderDao(): SenderDao
    abstract fun imageDao(): ImageDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao
}