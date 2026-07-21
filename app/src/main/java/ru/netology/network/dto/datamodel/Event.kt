package ru.netology.nework.dto

import ru.netology.network.dto.datamodel.Coordinates
import ru.netology.network.dto.datamodel.UserPreview

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,          // ? потому что в Swagger нет звёздочки — поле необязательное
    val authorAvatar: String? = null,
    val content: String,
    val datetime: String,                   // пока строкой, даты потом разберём
    val published: String,
    val coords: Coordinates? = null,        // вложенная модель
    val type: EventType,                    // это будет enum: OFFLINE/ONLINE
    val likeOwnerIds: List<Long>,           // uniqueItems: true → это список без дублей, то есть List
    val likedByMe: Boolean,
    val speakerIds: List<Long>,
    val participantsIds: List<Long>,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,      // вложенная модель
    val link: String? = null,
    val users: List<UserPreview>            // список пользователей
)

// Вложенные модели (тоже нужны как чертежи)





// Перечисления (Enum) — это как раз те самые [OFFLINE, ONLINE] и [IMAGE, VIDEO, AUDIO]
enum class EventType {
    OFFLINE, ONLINE
}


