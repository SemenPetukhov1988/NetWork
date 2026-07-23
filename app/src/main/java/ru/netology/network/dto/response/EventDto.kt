package ru.netology.network.dto.response

import com.google.gson.annotations.SerializedName

data class EventDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val datetime: String, // date-time
    val published: String, // date-time
    val coords: CoordinatesDto? = null,
    val type: EventType,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Long>,
    @SerializedName("likedByMe")
    val likedByMe: Boolean,
    @SerializedName("speakerIds")
    val speakerIds: List<Long>,
    @SerializedName("participantsIds")
    val participantsIds: List<Long>,
    @SerializedName("participatedByMe")
    val participatedByMe: Boolean,
    val attachment: AttachmentDto? = null,
    val link: String? = null,
    val users: Map<String, UserPreviewDto>? = null
)

enum class EventType {
    OFFLINE, ONLINE
}