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
import ru.netology.network.R

import ru.netology.network.dto.response.EventDto

class EventAdapter(
    private val onAuthorClick: (EventDto) -> Unit,
    private val onOptionsClick: (EventDto) -> Unit
) : PagingDataAdapter<EventDto, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false) // Убедись, что имя файла разметки именно item_event.xml
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.postAvatar)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.postAuthorName)
        private val tvDate: TextView = itemView.findViewById(R.id.postDate)
        private val btnOptions: ImageButton = itemView.findViewById(R.id.postOptionsBtn)

        private val ivEventImage: ImageView = itemView.findViewById(R.id.eventImage)
        private val tvEventType: TextView = itemView.findViewById(R.id.eventType)
        private val tvEventTime: TextView = itemView.findViewById(R.id.eventTime)
        private val tvText: TextView = itemView.findViewById(R.id.postText)

        private val tvLikeCount: TextView = itemView.findViewById(R.id.postLikeCount)
        private val tvParticipantCount: TextView = itemView.findViewById(R.id.postParticipantCount)

        fun bind(event: EventDto) {
            // Базовые данные
            tvAuthorName.text = event.author
            // Для ленты событий обычно показывают дату публикации (published), если нужно именно время события — поставь event.datetime
            tvDate.text = event.published

            itemView.setOnClickListener { onAuthorClick(event) }
            btnOptions.setOnClickListener { onOptionsClick(event) }

            // Аватар автора (круглый, как в посте)
            event.authorAvatar?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.load)
                    .error(R.drawable.smile)
                    .into(ivAvatar)
            } ?: run {
                Glide.with(itemView.context)
                    .load(R.drawable.avatar)
                    .circleCrop()
                    .into(ivAvatar)
            }

            // Фото события (основная картинка)
            val attachment = event.attachment
            if (attachment != null && attachment.type == "IMAGE") {
                ivEventImage.isVisible = true
                Glide.with(itemView.context)
                    .load(attachment.url)
                    .placeholder(R.drawable.load) // Серый квадрат-заглушка
                    .error(R.drawable.error)     // Красный/любой индикатор ошибки
                    .into(ivEventImage)
            } else {
                ivEventImage.isVisible = false
            }

            // Тип события (ONLINE/OFFLINE) и время события
            tvEventType.text = event.type.name // Превращает OFFLINE -> "OFFLINE"
            tvEventTime.text = event.datetime   // Время проведения события

            // Текст описания события
            tvText.text = event.content

            // Количество лайков
            tvLikeCount.text = event.likeOwnerIds.size.toString()

            // Количество участников
            tvParticipantCount.text = event.participantsIds.size.toString()
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<EventDto>() {
    override fun areItemsTheSame(oldItem: EventDto, newItem: EventDto): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EventDto, newItem: EventDto): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.content == newItem.content &&
                oldItem.likedByMe == newItem.likedByMe &&
                oldItem.participatedByMe == newItem.participatedByMe &&
                oldItem.likeOwnerIds.size == newItem.likeOwnerIds.size &&
                oldItem.participantsIds.size == newItem.participantsIds.size &&
                (oldItem.attachment?.url == newItem.attachment?.url) &&
                oldItem.type == newItem.type
    }
}
