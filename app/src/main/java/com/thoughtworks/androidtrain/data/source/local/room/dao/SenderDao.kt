package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.SenderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SenderDao {
    @Query("SELECT * FROM sender")
    fun findAll(): Flow<List<SenderEntity>>

    @Query("SELECT * FROM sender")
    fun findAll1(): List<SenderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg senders: SenderEntity): List<Long>
}