package ru.netology.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.network.R
import ru.netology.network.databinding.ItemUserBinding
import ru.netology.network.dto.response.UserDto

class UsersAdapter(
    private val onItemClick: (UserDto) -> Unit
) : ListAdapter<UserDto, UsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
        holder.bind(user)
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserDto) {
            // Имя и ник
            binding.userName.text = user.name
            binding.userNick.text = user.login

            // Клик по всей карточке → переход в профиль
            itemView.setOnClickListener {
                onItemClick(user)
            }

            // Аватар: загружаем и делаем круглым
            user.avatar?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.smile)
                    .into(binding.userAvatar)
            } ?: run {
                // Если аватара нет — ставим заглушку
                Glide.with(itemView.context)
                    .load(R.drawable.avatar)
                    .transform(CircleCrop())
                    .into(binding.userAvatar)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<UserDto>() {
    override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean =
        oldItem == newItem
}
