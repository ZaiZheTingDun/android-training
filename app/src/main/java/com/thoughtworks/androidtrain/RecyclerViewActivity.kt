package com.thoughtworks.androidtrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.thoughtworks.androidtrain.adapters.TweetAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RecyclerViewActivity : AppCompatActivity() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val fetchTweets = AppApplication.getTweetRepository().fetchTweets()
        compositeDisposable.add(
            fetchTweets.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                recyclerView.adapter = TweetAdapter(it)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}