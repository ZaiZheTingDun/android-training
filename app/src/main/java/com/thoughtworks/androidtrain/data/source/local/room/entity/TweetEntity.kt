package com.thoughtworks.androidtrain.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tweet")
data class TweetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val content: String,
    @ColumnInfo(name = "sender_id") val senderId: Long
)
