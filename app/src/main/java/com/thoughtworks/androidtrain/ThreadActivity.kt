package com.thoughtworks.androidtrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button

class ThreadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        val button = findViewById<Button>(R.id.thread_button)
        button.setOnClickListener {
            startCounterThread(button)
        }
    }

    private fun startCounterThread(button: Button) {
        val thread = Thread {
            runOnUiThread { button.isEnabled = false }
            for (i in 0..10) {
                runOnUiThread { button.text = i.toString() }
                SystemClock.sleep(1000)
            }
            runOnUiThread { button.isEnabled = true }
        }
        thread.start()
    }
}
