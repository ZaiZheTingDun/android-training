package com.thoughtworks.androidtrain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.thoughtworks.androidtrain.data.model.Comment
import com.thoughtworks.androidtrain.data.model.Image
import com.thoughtworks.androidtrain.data.model.Sender
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.source.ITweetRepository
import com.thoughtworks.androidtrain.viewmodels.TweetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ExampleUnitTest {
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Test
    fun should_get_tweet_data_via_tweet_repository_fetch_data() {
        val tweet = Tweet(
            "content1",
            listOf(Image("image url1")),
            Sender("mingli", "nick1", "avatar1"),
            listOf(Comment("comment content1", Sender("user1", "nick2", "avatar2")))
        )

        val tweetRepository = mock(ITweetRepository::class.java)
        `when`(tweetRepository.fetchTweets()).thenReturn(flow { emit(listOf(tweet)) })

        val value = TweetViewModel(tweetRepository).also { it.tweets.observeForever {} }.tweets.value

        assertThat(value).isNotNull()
        assertThat(value).hasSize(1)
        assertThat(value!![0]).isEqualTo(tweet)
    }

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}