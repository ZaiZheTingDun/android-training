package com.thoughtworks.androidtrain.data.source

import androidx.room.withTransaction
import com.thoughtworks.androidtrain.data.model.*
import com.thoughtworks.androidtrain.data.source.local.datasource.TweetDataSource
import com.thoughtworks.androidtrain.data.source.local.datasource.UserDataSource
import com.thoughtworks.androidtrain.data.source.local.room.AppDatabase
import com.thoughtworks.androidtrain.data.source.local.room.dao.*
import com.thoughtworks.androidtrain.data.source.local.room.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TweetRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val tweetDataSource: TweetDataSource,
    private val userDataSource: UserDataSource,
    private val senderDao: SenderDao,
    private val imageDao: ImageDao,
    private val commentDao: CommentDao,
    private val tweetDao: TweetDao,
    private val userDao: UserDao
) : ITweetRepository {
    override fun updateData() {
        runBlocking(Dispatchers.IO) {
            appDatabase.clearAllTables()
            updateUser()
            updateTweets()
        }
    }

    override fun fetchUser() = getUser()

    override fun fetchTweets() = getTweets()

    private fun getUser() = userDao.get().map { userList ->
        userList.singleOrNull()?.let {
            User(it.profileImage, it.avatar, it.nick, it.username)
        }
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

    private suspend fun updateUser() {
        try {
            val user = userDataSource.loadData()
            appDatabase.withTransaction {
                val userEntity = UserEntity(0L, user.profileImage, user.avatar, user.nick, user.username)
                insertUser(userEntity)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun updateTweets() {
        try {
            val tweets = tweetDataSource.loadData()
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

    private suspend fun insertUser(userEntity: UserEntity) = userDao.insert(userEntity)
}