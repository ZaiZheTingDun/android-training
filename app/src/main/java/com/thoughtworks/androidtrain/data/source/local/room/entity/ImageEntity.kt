package com.thoughtworks.androidtrain.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val url: String,
    @ColumnInfo(name = "tweet_id") val tweetId: Long
)
