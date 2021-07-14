package com.thoughtworks.androidtrain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_SELECT_CONTACT = 1
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity Created!")

        findViewById<Button>(R.id.button1).setOnClickListener {
            startActivity(Intent(this, ConstraintActivity::class.java))
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            selectContact()
        }

        findViewById<Button>(R.id.button4).setOnClickListener {
            startActivity(Intent(this, MyFragmentActivity::class.java))
        }

        findViewById<Button>(R.id.button5).setOnClickListener {
            startActivity(Intent(this, RecyclerViewActivity::class.java))
        }

        findViewById<Button>(R.id.button6).setOnClickListener {
            startActivity(Intent(this, ThreadActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK && data != null) {
            val contactUri = data.data
            if (contactUri != null) {
                val projection = arrayOf(
                    ContactsContract.Data._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                val query = contentResolver.query(contactUri, projection, null, null, null)
                query?.use {
                    it.moveToNext()

                    val displayName = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    Toast.makeText(this, "$displayName $phoneNumber", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT)
        }
    }
}