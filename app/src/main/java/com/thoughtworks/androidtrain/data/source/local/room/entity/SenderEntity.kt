package com.thoughtworks.androidtrain.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sender")
data class SenderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val username: String,
    @ColumnInfo val nick: String,
    @ColumnInfo val avatar: String
)
