package com.thoughtworks.androidtrain.data.source

import androidx.room.withTransaction
import com.thoughtworks.androidtrain.data.model.Comment
import com.thoughtworks.androidtrain.data.model.Image
import com.thoughtworks.androidtrain.data.model.Sender
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.data.source.local.datasource.TweetDataSource
import com.thoughtworks.androidtrain.data.source.local.room.AppDatabase
import com.thoughtworks.androidtrain.data.source.local.room.dao.CommentDao
import com.thoughtworks.androidtrain.data.source.local.room.dao.ImageDao
import com.thoughtworks.androidtrain.data.source.local.room.dao.SenderDao
import com.thoughtworks.androidtrain.data.source.local.room.dao.TweetDao
import com.thoughtworks.androidtrain.data.source.local.room.entity.CommentEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.ImageEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.SenderEntity
import com.thoughtworks.androidtrain.data.source.local.room.entity.TweetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TweetRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val tweetDataSource: TweetDataSource,
    private val senderDao: SenderDao,
    private val imageDao: ImageDao,
    private val commentDao: CommentDao,
    private val tweetDao: TweetDao
) : ITweetRepository {

    override fun fetchTweets(): Flow<List<Tweet>> {
        runBlocking(Dispatchers.IO) { updateTweets() }
        return getTweets()
    }

    private fun getTweets(): Flow<List<Tweet>> {
        return tweetDao.findAll().map { tweetEntities ->
            val senderEntities = senderDao.findAll().first()
            val commentEntities = commentDao.findAll().first()
            val imageEntities = imageDao.findAll().first()

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

    private suspend fun insertTweet(tweetEntity: TweetEntity) = tweetDao.insert(tweetEntity).single()

    private suspend fun insertSender(senderEntity: SenderEntity) = senderDao.insert(senderEntity).single()

    private suspend fun insertComment(commentEntity: CommentEntity) = commentDao.insert(commentEntity).single()

    private suspend fun insertImage(imageEntity: ImageEntity) = imageDao.insert(imageEntity).single()
}