package ru.netology.network.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.network.R
import ru.netology.network.dto.response.PostDto

class PostAdapter(
    private val onAuthorClick: (PostDto) -> Unit,
    private val onOptionsClick: (PostDto) -> Unit
) : PagingDataAdapter<PostDto, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.postAvatar)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.postAuthorName)
        private val tvDate: TextView = itemView.findViewById(R.id.postDate)
        private val btnOptions: ImageButton = itemView.findViewById(R.id.postOptionsBtn)
        private val ivPostImage: ImageView = itemView.findViewById(R.id.postImage)
        private val tvText: TextView = itemView.findViewById(R.id.postText)

        fun bind(post: PostDto) {
            tvAuthorName.text = post.author
            tvDate.text = post.published

            itemView.setOnClickListener { onAuthorClick(post) }
            btnOptions.setOnClickListener { onOptionsClick(post) }

            // Аватар
            post.authorAvatar?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .circleCrop()                 // <-- сразу после load, до placeholder/error
                    .placeholder(R.drawable.load)
                    .error(R.drawable.smile)
                    .into(ivAvatar)
            } ?: run {
                Glide.with(itemView.context)
                    .load(R.drawable.avatar)
                    .circleCrop()
                    .into(ivAvatar)
            }

            // Фото поста
            val attachment = post.attachment
            if (attachment != null && attachment.type == "IMAGE") {
                ivPostImage.isVisible = true
                Glide.with(itemView.context)
                    .load(attachment.url)
                    .placeholder(R.drawable.load)
                    .error(R.drawable.error)
                    .into(ivPostImage)
            } else {
                ivPostImage.isVisible = false
            }

            tvText.text = post.content
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<PostDto>() {
    override fun areItemsTheSame(oldItem: PostDto, newItem: PostDto): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.content == newItem.content &&
                oldItem.likedByMe == newItem.likedByMe &&
                oldItem.likeOwnerIds.size == newItem.likeOwnerIds.size &&
                (oldItem.attachment?.url == newItem.attachment?.url)
    }
}
