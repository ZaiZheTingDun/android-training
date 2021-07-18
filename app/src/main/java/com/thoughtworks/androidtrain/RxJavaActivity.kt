package com.thoughtworks.androidtrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RxJavaActivity : AppCompatActivity() {
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java)

        findViewById<Button>(R.id.rxjava_button).apply {
            setOnClickListener {
                counter(this)
            }
        }
    }

    private fun counter(button: Button) {
        val subscribe = Observable.create<String> {
            button.post { button.isEnabled = false }
            for (i in 0..10) {
                it.onNext(i.toString())
                SystemClock.sleep(1000)
            }
            it.onComplete()
        }.map {
            "The number is $it"
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ button.post { button.text = it } }, {}, { button.post { button.isEnabled = true } })
        compositeDisposable.add(subscribe)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}