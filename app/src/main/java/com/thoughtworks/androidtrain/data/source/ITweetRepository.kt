package com.thoughtworks.androidtrain.data.source

import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.model.User
import kotlinx.coroutines.flow.Flow

interface ITweetRepository {
    fun fetchTweets(): Flow<List<Tweet>>
    fun fetchUser(): Flow<User?>
    fun updateData()
}