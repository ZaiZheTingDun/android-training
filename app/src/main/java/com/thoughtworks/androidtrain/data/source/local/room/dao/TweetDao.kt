package com.thoughtworks.androidtrain.data.source.local.room.dao

import androidx.room.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.TweetEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface TweetDao {
    @Transaction
    @Query("SELECT * FROM tweet")
    fun findAll(): Flowable<List<TweetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tweets: TweetEntity): Single<List<Long>>
}