package com.thoughtworks.androidtrain

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.thoughtworks.androidtrain.adapters.MomentsAdapter
import com.thoughtworks.androidtrain.di.BackgroundScope
import com.thoughtworks.androidtrain.headers.MomentsRefreshHeader
import com.thoughtworks.androidtrain.viewmodels.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MomentsActivity : AppCompatActivity() {
    @Inject @BackgroundScope lateinit var backgroundScope: CoroutineScope
    private val tweetViewModel: TweetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moments)

        val refreshLayout = findViewById<View>(R.id.moment_smart_refresh_layout) as RefreshLayout
        val momentsRefreshHeader = refreshLayout.refreshHeader as MomentsRefreshHeader

        val momentsContentRecyclerView = findViewById<RecyclerView>(R.id.moment_content_recycler_view)

        val width = this.window.decorView.width
        tweetViewModel.user.observe(this, {
            it?.let {
                backgroundScope.launch(Dispatchers.IO) {
                    val avatarView = momentsRefreshHeader.profileAvatarView
                    val usernameView = momentsRefreshHeader.profileUsernameView
                    val profileBackgroundView = momentsRefreshHeader.profileBackgroundView
                    val widthAndHeightLength = (48F * applicationContext.resources.displayMetrics.density).toInt()

                    val avatarDrawable = GlideApp
                        .with(avatarView.context)
                        .load(it.avatar)
                        .submit(widthAndHeightLength, widthAndHeightLength)
                        .get()

                    val profileImageDrawable = GlideApp
                        .with(avatarView.context)
                        .load(it.profileImage)
                        .submit((300F * applicationContext.resources.displayMetrics.density).toInt(), width)
                        .get()

                    avatarView.post {
                        avatarView.setImageDrawable(avatarDrawable)
                    }
                    profileBackgroundView.post {
                        profileBackgroundView.setImageDrawable(profileImageDrawable)
                    }
                    usernameView.post {
                        usernameView.text = it.username
                    }
                }
            }
        })

        tweetViewModel.tweets.observe(this, {
            momentsContentRecyclerView.adapter = MomentsAdapter(it, backgroundScope)
        })

        momentsRefreshHeader.setOnRefresh {
            tweetViewModel.updateData()
        }
    }
}