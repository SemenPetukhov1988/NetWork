package ru.netology.network.dto.response

data class EventDto(val id: Long,
                    val authorId: Long,
                    val author: String,
                    val authorJob: String,
                    val authorAvatar: String?,
                    val content: String,
                    val datetime: String,          // ISO формат
                    val published: String,        // ISO формат
                    val coords: CoordsDto?,       // Вынесем координаты отдельно
                    val type: EventType,          // Лучше использовать Enum, но пока можно String
                    val likeOwnerIds: List<Long>,
                    val likedByMe: Boolean,
                    val speakerIds: List<Long>,
                    val participantsIds: List<Long>,
                    val participatedByMe: Boolean,
                    val attachment: AttachmentDto?,
                    val link: String,
                    val users: Map<String, UserPreviewDto>? // "users" из твоего JSON
)

data class CoordsEventDto(
    val lat: Double,
    val long: Double
)

data class AttachmentEventDto(
    val url: String,
    val type: String // IMAGE, VIDEO и т.д.
)

data class UserPreviewDto(
    val name: String,
    val avatar: String?
)

// Опционально: Enum для типа события, если бэкенд строгий
enum class EventType { OFFLINE, ONLINE }
