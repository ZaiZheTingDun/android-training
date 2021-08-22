package com.thoughtworks.androidtrain

import android.app.Application
import com.thoughtworks.androidtrain.data.source.ITweetRepository
import com.thoughtworks.androidtrain.data.source.TweetRepository

class AppApplication : Application() {
    companion object {
        private lateinit var tweetRepository: ITweetRepository

        fun getTweetRepository(): ITweetRepository = tweetRepository
    }

    override fun onCreate() {
        super.onCreate()
        tweetRepository = TweetRepository(applicationContext)
    }
}