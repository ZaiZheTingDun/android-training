package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.SenderEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface SenderDao {
    @Query("SELECT * FROM sender")
    fun findAll(): Flowable<List<SenderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg senders: SenderEntity): Single<List<Long>>
}