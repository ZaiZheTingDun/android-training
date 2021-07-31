package com.thoughtworks.androidtrain

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class SpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp)

        val button = findViewById<Button>(R.id.sp_get_it_button)
        button.setOnClickListener {
            setIsKnown()
            finish()
        }

        val textView = findViewById<TextView>(R.id.sp_text)

        val isKnown = getIsKnown()
        if (isKnown) {
            textView.text = resources.getText(R.string.sp_content)
            button.visibility =View.GONE
        } else {
            textView.text = resources.getText(R.string.sp_prompt_information)
        }
    }

    private fun setIsKnown() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("isKnown", true)
            apply()
        }
    }

    private fun getIsKnown() : Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return false
        return sharedPref.getBoolean("isKnown", false)
    }
}