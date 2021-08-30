package com.thoughtworks.androidtrain.viewmodels

import androidx.lifecycle.*
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.source.ITweetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TweetViewModel @Inject constructor(
    private val tweetRepository: ITweetRepository
) : ViewModel() {
    var tweets: LiveData<List<Tweet>> = MutableLiveData()

    init {
        viewModelScope.launch {
            tweets = tweetRepository.fetchTweets().asLiveData()
        }
    }
}