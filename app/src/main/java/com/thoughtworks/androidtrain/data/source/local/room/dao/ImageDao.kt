package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun findAll(): Flow<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg images: ImageEntity): List<Long>
}