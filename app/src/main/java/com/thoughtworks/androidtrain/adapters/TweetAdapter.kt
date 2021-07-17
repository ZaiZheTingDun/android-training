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

class TweetAdapter(private val tweetItems: List<Tweet>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val CONTENT_VIEW_TYPE = 0
        private const val FOOTER_VIEW_TYPE = 1
    }

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountNameTextView: TextView = view.findViewById(R.id.account_name)
        val contentTextView: TextView = view.findViewById(R.id.tweet_content)
        val avatarImageView: ImageView = view.findViewById(R.id.avatar)
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CONTENT_VIEW_TYPE -> {
                ContentViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_tweet_content, parent, false)
                )
            }
            else -> {
                FooterViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_tweet_footer, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentViewHolder) {
            holder.accountNameTextView.text = tweetItems[position].sender.nick
            holder.contentTextView.text = tweetItems[position].content ?: ""

            Thread {
                val avatarImageView = holder.avatarImageView
                val drawable = GlideApp
                    .with(avatarImageView.context)
                    .load(tweetItems[position].sender.avatar)
                    .submit(48, 48)
                    .get()
                avatarImageView.post {
                    avatarImageView.setImageDrawable(drawable)
                }
            }.start()
        }
    }

    override fun getItemCount() = tweetItems.size + 1

    override fun getItemViewType(position: Int): Int = if (position != itemCount - 1) CONTENT_VIEW_TYPE else FOOTER_VIEW_TYPE
}