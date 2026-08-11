package ru.netology.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.network.databinding.ItemWorkBinding
import ru.netology.network.dto.response.JobResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class JobsAdapter(
    private val onItemClick: (JobResponse) -> Unit
) : ListAdapter<JobResponse, JobsAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemWorkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position) ?: return
        holder.bind(job)
    }

    inner class JobViewHolder(private val binding: ItemWorkBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(job: JobResponse) {
            // Название компании/проекта
            binding.tvCompanyName.text = job.name

            // Форматируем даты
            val startFormatted = formatDate(job.start)
            val finishFormatted = if (job.finish.isNullOrBlank()) "—" else formatDate(job.finish)

            binding.tvPeriod.text = "$startFormatted — $finishFormatted"

            // Должность
            binding.tvPosition.text = job.position ?: "Без должности"

            // Клик по карточке
            itemView.setOnClickListener {
                onItemClick(job)
            }
        }
    }

    /**
     * Превращает ISO-строку (2026-08-11T20:32:09.718Z) в читаемый формат (11 августа 2026)
     */
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "Нет даты"

        return try {
            val instant = Instant.parse(dateString)
            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
        } catch (e: Exception) {
            // Если вдруг формат строки не тот — показываем как есть, чтобы приложение не упало
            dateString
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<JobResponse>() {
    override fun areItemsTheSame(oldItem: JobResponse, newItem: JobResponse): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: JobResponse, newItem: JobResponse): Boolean =
        oldItem == newItem
}
