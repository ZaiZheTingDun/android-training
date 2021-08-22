package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.ImageEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun findAll(): Flowable<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg images: ImageEntity): Single<List<Long>>
}