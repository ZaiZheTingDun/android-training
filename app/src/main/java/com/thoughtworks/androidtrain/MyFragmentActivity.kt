package com.thoughtworks.androidtrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.thoughtworks.androidtrain.fragments.AndroidFragment
import com.thoughtworks.androidtrain.fragments.JavaFragment

class MyFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_fragment)

        findViewById<Button>(R.id.android_button).setOnClickListener {
            switchFragment(AndroidFragment())
        }

        findViewById<Button>(R.id.java_button).setOnClickListener {
            switchFragment(JavaFragment())
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fragment_content, fragment, null)
        beginTransaction.addToBackStack(null)
        beginTransaction.commitAllowingStateLoss()
    }
}