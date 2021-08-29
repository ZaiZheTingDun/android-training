package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.TweetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TweetDao {
    @Query("SELECT * FROM tweet")
    fun findAll(): Flow<List<TweetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg tweets: TweetEntity): List<Long>
}