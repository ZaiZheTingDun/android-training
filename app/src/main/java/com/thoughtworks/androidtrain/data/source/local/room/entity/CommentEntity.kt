package com.thoughtworks.androidtrain.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "sender_id") val senderId: Long,
    @ColumnInfo(name = "tweet_id") val tweetId: Long
)
