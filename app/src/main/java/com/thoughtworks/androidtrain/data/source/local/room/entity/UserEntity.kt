package com.thoughtworks.androidtrain.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "profile_image") val profileImage: String,
    @ColumnInfo(name = "avatar") val avatar: String,
    @ColumnInfo(name = "nick") val nick: String,
    @ColumnInfo(name = "username") val username: String
)

