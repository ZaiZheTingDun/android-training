package com.thoughtworks.androidtrain.data.source

import com.thoughtworks.androidtrain.data.model.Tweet
import io.reactivex.rxjava3.core.Flowable

interface ITweetRepository {
    fun fetchTweets(): Flowable<List<Tweet>>
}