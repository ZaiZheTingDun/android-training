package com.thoughtworks.androidtrain.data.source.local.datasource

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thoughtworks.androidtrain.data.model.Tweet
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TweetDataSource @Inject constructor(
    private val httpClient: OkHttpClient,
    @ApplicationContext private val context: Context
) {
    suspend fun loadData(): List<Tweet> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(URL).build()
            var responseData = ""
            try {
                responseData = httpClient.newCall(request).execute().use { response -> response.body!!.string() }
            } catch (e: Exception) {
                Looper.prepare()
                Toast.makeText(context, "load tweet data failed.", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
            deserializationData(responseData)
        }
    }

    companion object {
        private const val URL = "https://thoughtworks-mobile-2018.herokuapp.com/user/jsmith/tweets"

        private fun deserializationData(data: String): List<Tweet> {
            val gson = Gson()
            val type = object : TypeToken<List<Tweet>>() {}.type
            return gson.fromJson(data, type)
        }
    }
}