package com.thoughtworks.androidtrain

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class HandlerActivity : AppCompatActivity() {
    private companion object {
        private const val BUTTON1 = 1
        private const val BUTTON2 = 2
    }

    private lateinit var handlerThread : HandlerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler)

        handlerThread = HandlerThread("handlerThread").apply { start() }

        val mainHandler = Handler(Looper.getMainLooper())
        val workHandler = Handler(handlerThread.looper) {
            when (it.what) {
                BUTTON1 -> mainHandler.post { Toast.makeText(this, "Button 1", Toast.LENGTH_SHORT).show() }
                BUTTON2 -> mainHandler.post { Toast.makeText(this, "Button 2", Toast.LENGTH_SHORT).show() }
                else -> true
            }
        }

        findViewById<Button>(R.id.handler_button1).setOnClickListener {
            workHandler.sendMessage(Message.obtain().apply {
                what = BUTTON1
            })
        }

        findViewById<Button>(R.id.handler_button2).setOnClickListener {
            workHandler.sendMessage(Message.obtain().apply {
                what = BUTTON2
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
    }
}