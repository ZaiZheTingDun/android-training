package com.thoughtworks.androidtrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thoughtworks.androidtrain.R
import com.thoughtworks.androidtrain.data.model.Tweet

class TweetAdapter(private val tweetItems: List<Tweet>) :
    RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountNameTextView: TextView = view.findViewById(R.id.account_name)
        val contentTextView: TextView = view.findViewById(R.id.tweet_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tweet_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.accountNameTextView.text = tweetItems[position].sender.nick
        holder.contentTextView.text = tweetItems[position].content ?: ""
    }

    override fun getItemCount() = tweetItems.size
}