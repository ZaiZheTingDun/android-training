package com.thoughtworks.androidtrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thoughtworks.androidtrain.adapters.TweetAdapter
import com.thoughtworks.androidtrain.data.model.Tweet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStreamReader

class RecyclerViewActivity : AppCompatActivity() {
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val subscribe = Observable.create<String> {
            val readText = InputStreamReader(resources.openRawResource(R.raw.tweets)).readText()
            it.onNext(readText)
            it.onComplete()
        }.map {
            deserializationData(it)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val data = it.filter { tweet -> tweet.error == null && tweet.unknownError == null }
                recyclerView.adapter = TweetAdapter(data)
            }

        compositeDisposable.add(subscribe)
    }

    private fun deserializationData(data: String) : List<Tweet> {
        val gson = Gson()
        val type = object : TypeToken<List<Tweet>>() {}.type
        return gson.fromJson(data, type)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}