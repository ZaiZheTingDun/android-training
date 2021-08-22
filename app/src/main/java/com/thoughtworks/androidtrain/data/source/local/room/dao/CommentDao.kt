package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thoughtworks.androidtrain.data.source.local.room.entity.CommentEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface CommentDao {
    @Query("SELECT * FROM comment")
    fun findAll(): Flowable<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg comments: CommentEntity): Single<List<Long>>
}