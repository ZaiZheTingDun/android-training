package com.thoughtworks.androidtrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.thoughtworks.androidtrain.GlideApp
import com.thoughtworks.androidtrain.R
import com.thoughtworks.androidtrain.data.model.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MomentImageAdapter(
    private val images: List<Image>,
    private val backgroundScope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentImageImageView: ImageView = view.findViewById(R.id.moment_content_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_moment_image, parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ContentViewHolder
        backgroundScope.launch(Dispatchers.IO) {
            val avatarImageView = holder.contentImageImageView
            val drawable = GlideApp
                .with(avatarImageView.context)
                .load(images[position].url)
                .submit(48, 48)
                .get()
            avatarImageView.post {
                avatarImageView.setImageDrawable(drawable)
            }
        }
    }

    override fun getItemCount() = images.size
}