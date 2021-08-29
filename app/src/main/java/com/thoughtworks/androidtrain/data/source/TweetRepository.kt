package com.thoughtworks.androidtrain.data.source

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.thoughtworks.androidtrain.data.model.Comment
import com.thoughtworks.androidtrain.data.model.Image
import com.thoughtworks.androidtrain.data.model.Sender
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.source.local.datasource.TweetDataSource
import com.thoughtworks.androidtrain.data.source.local.room.AppDatabase
import com.thoughtworks.androidtrain.data.source.local.room.entity.CommentEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.ImageEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.SenderEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.TweetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class TweetRepository(context: Context) : ITweetRepository {
    private val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "practice-db").build()
    private val tweetDataSource = TweetDataSource(context)

    override fun fetchTweets(): Flow<List<Tweet>> {
        runBlocking(Dispatchers.IO) { updateTweets() }
        return getTweets()
    }

    private fun getTweets(): Flow<List<Tweet>> {
        return appDatabase.tweetDao().findAll().map { tweetEntities ->
            val senderEntities = appDatabase.senderDao().findAll().first()
            val commentEntities = appDatabase.commentDao().findAll().first()
            val imageEntities = appDatabase.imageDao().findAll().first()

            tweetEntities.map { tweetEntity ->
                Tweet().apply {
                    content = tweetEntity.content
                    sender = senderEntities.single { tweetEntity.senderId == it.id }.let { senderEntity -> Sender(senderEntity.username, senderEntity.nick, senderEntity.avatar) }
                    comments = commentEntities.filter { it.tweetId == tweetEntity.id }.map { commentEntity ->
                        val commentSender = senderEntities.single { commentEntity.senderId == it.id }.let { senderEntity -> Sender(senderEntity.username, senderEntity.nick, senderEntity.avatar) }
                        Comment(commentEntity.content, commentSender)
                    }
                    images = imageEntities.filter { it.tweetId == tweetEntity.id }.map { Image(it.url) }
                }
            }
        }
    }

    private suspend fun updateTweets() {
        try {
            val tweets = tweetDataSource.loadData()
            appDatabase.clearAllTables()
            appDatabase.withTransaction {
                tweets
                    .filter { tweet -> tweet.error == null && tweet.unknownError == null && tweet.content != null }
                    .map { tweet ->
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
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun insertTweet(tweetEntity: TweetEntity) = appDatabase.tweetDao().insert(tweetEntity).single()

    private suspend fun insertSender(senderEntity: SenderEntity) = appDatabase.senderDao().insert(senderEntity).single()

    private suspend fun insertComment(commentEntity: CommentEntity) = appDatabase.commentDao().insert(commentEntity).single()

    private suspend fun insertImage(imageEntity: ImageEntity) = appDatabase.imageDao().insert(imageEntity).single()
}