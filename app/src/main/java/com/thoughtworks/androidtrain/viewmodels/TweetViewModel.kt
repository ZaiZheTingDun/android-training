package com.thoughtworks.androidtrain.viewmodels

import androidx.lifecycle.*
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.model.User
import com.thoughtworks.androidtrain.data.source.ITweetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TweetViewModel @Inject constructor(
    private val tweetRepository: ITweetRepository
) : ViewModel() {
    var tweets: LiveData<List<Tweet>> = MutableLiveData()
    var user: LiveData<User?> = MutableLiveData()

    init {
        viewModelScope.launch {
            tweets = tweetRepository.fetchTweets().asLiveData()
            user = tweetRepository.fetchUser().asLiveData()
        }
    }

    fun updateData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tweetRepository.updateData()
            }
        }
    }
}