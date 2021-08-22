package com.thoughtworks.androidtrain.data.source

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thoughtworks.androidtrain.R
import com.thoughtworks.androidtrain.data.model.Comment
import com.thoughtworks.androidtrain.data.model.Image
import com.thoughtworks.androidtrain.data.model.Sender
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.source.local.room.AppDatabase
import com.thoughtworks.androidtrain.data.source.local.room.entity.CommentEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.ImageEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.SenderEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.TweetEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStreamReader
import java.lang.Exception

class TweetRepository(private val context: Context) : ITweetRepository {
    private val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "practice-db").build()

    override fun fetchTweets(): Flowable<List<Tweet>> {
        updateDatabase().subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe()
        return getTweets()
    }

    private fun getTweets(): Flowable<List<Tweet>> {
        return appDatabase.tweetDao().findAll().map { tweetEntities ->
            val senderEntities = appDatabase.senderDao().findAll().blockingFirst()
            val commentEntities = appDatabase.commentDao().findAll().blockingFirst()
            val imageEntities = appDatabase.imageDao().findAll().blockingFirst()

            tweetEntities.map { tweetEntity ->
                val tweet = Tweet()
                tweet.content = tweetEntity.content
                tweet.sender = senderEntities.single { tweetEntity.senderId == it.id }.let { senderEntity -> Sender(senderEntity.username, senderEntity.nick, senderEntity.avatar) }
                tweet.comments = commentEntities.filter { it.tweetId == tweetEntity.id }.map { commentEntity ->
                    val commentSender = senderEntities.single { commentEntity.senderId == it.id }.let { senderEntity -> Sender(senderEntity.username, senderEntity.nick, senderEntity.avatar) }
                    Comment(commentEntity.content, commentSender)
                }
                tweet.images = imageEntities.filter { it.tweetId == tweetEntity.id }.map { Image(it.url) }
                tweet
            }
        }
    }

    private fun updateDatabase(): Single<Boolean> {
        val readText = InputStreamReader(context.resources.openRawResource(R.raw.tweets)).readText()
        val tweets = deserializationData(readText)

        return Single.create {
            try {
                appDatabase.clearAllTables()
                appDatabase.runInTransaction {
                    tweets
                        .filter { tweet -> tweet.error == null && tweet.unknownError == null && tweet.content != null }
                        .forEach { tweet ->
                            val senderEntity = SenderEntity(0L, tweet.sender.username, tweet.sender.nick, tweet.sender.avatar)
                            val senderId = insertSender(senderEntity)

                            val tweetEntity = TweetEntity(0L, tweet.content, senderId)
                            val tweetId = insertTweet(tweetEntity)

                            tweet.comments?.forEach { comment ->
                                val commentsSenderEntity = SenderEntity(0L, tweet.sender.username, tweet.sender.nick, tweet.sender.avatar)
                                val commentSenderId = insertSender(commentsSenderEntity)
                                val commentEntity = CommentEntity(0L, comment.content, commentSenderId, tweetId)
                                insertComment(commentEntity)
                            }

                            tweet.images?.forEach { image ->
                                val imageEntity = ImageEntity(0, image.url, tweetId)
                                insertImage(imageEntity)
                            }
                        }
                }
                it.onSuccess(true)
            } catch (e: Exception) {
                it.onError(e)
            }

        }
    }

    private fun deserializationData(data: String): List<Tweet> {
        val gson = Gson()
        val type = object : TypeToken<List<Tweet>>() {}.type
        return gson.fromJson(data, type)
    }

    private fun insertTweet(tweetEntity: TweetEntity) = appDatabase.tweetDao().insert(tweetEntity).blockingGet().single()

    private fun insertSender(senderEntity: SenderEntity) = appDatabase.senderDao().insert(senderEntity).blockingGet().single()

    private fun insertComment(commentEntity: CommentEntity) = appDatabase.commentDao().insert(commentEntity).blockingGet().single()

    private fun insertImage(imageEntity: ImageEntity) = appDatabase.imageDao().insert(imageEntity).blockingGet().single()
}