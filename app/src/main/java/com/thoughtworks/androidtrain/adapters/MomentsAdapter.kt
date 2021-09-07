package com.thoughtworks.androidtrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thoughtworks.androidtrain.GlideApp
import com.thoughtworks.androidtrain.R
import com.thoughtworks.androidtrain.data.model.Tweet
import com.thoughtworks.androidtrain.di.BackgroundScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MomentsAdapter(
    private val tweetItems: List<Tweet>,
    private val backgroundScope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val CONTENT_VIEW_TYPE = 0
    }

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountNameTextView: TextView = view.findViewById(R.id.moment_content_account_name)
        val contentTextView: TextView = view.findViewById(R.id.moment_content_content)
        val avatarImageView: ImageView = view.findViewById(R.id.moment_content_avatar)
        val contentImagesRecyclerView: RecyclerView = view.findViewById(R.id.moment_content_images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_moment_content, parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentViewHolder) {
            holder.accountNameTextView.text = tweetItems[position].sender.nick
            holder.contentTextView.text = tweetItems[position].content ?: ""

            backgroundScope.launch(Dispatchers.IO) {
                val avatarImageView = holder.avatarImageView
                val drawable = GlideApp
                    .with(avatarImageView.context)
                    .load(tweetItems[position].sender.avatar)
                    .submit(48, 48)
                    .get()
                avatarImageView.post {
                    avatarImageView.setImageDrawable(drawable)
                }
            }

            holder.contentImagesRecyclerView.adapter = MomentImageAdapter(tweetItems[position].images, backgroundScope)
        }
    }

    override fun getItemCount() = tweetItems.size

    override fun getItemViewType(position: Int) = CONTENT_VIEW_TYPE
}