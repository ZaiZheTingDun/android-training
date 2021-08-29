package com.thoughtworks.androidtrain

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.thoughtworks.androidtrain.adapters.TweetAdapter
import com.thoughtworks.androidtrain.viewmodels.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecyclerViewActivity : AppCompatActivity() {
    private val tweetViewModel: TweetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        tweetViewModel.tweets.observe(this, {
            recyclerView.adapter = TweetAdapter(it)
        })
    }
}