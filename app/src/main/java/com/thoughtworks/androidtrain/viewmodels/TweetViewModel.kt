package com.thoughtworks.androidtrain.viewmodels

import androidx.lifecycle.*
import com.thoughtworks.androidtrain.AppApplication
import com.thoughtworks.androidtrain.data.model.Tweet
import kotlinx.coroutines.launch

class TweetViewModel: ViewModel() {
    private val tweetRepository = AppApplication.getTweetRepository()
    var tweets: LiveData<List<Tweet>> = MutableLiveData()

    init {
        viewModelScope.launch {
            tweets = tweetRepository.fetchTweets().asLiveData()
        }
    }
}