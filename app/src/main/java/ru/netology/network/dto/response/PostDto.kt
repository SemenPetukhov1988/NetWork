package ru.netology.network.dto.response

import com.google.gson.annotations.SerializedName

data class PostDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val published: String, // В формате ISO 8601 (date-time)
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Long>,
    @SerializedName("likedByMe")
    val likedByMe: Boolean,
    @SerializedName("mentionIds")
    val mentionIds: List<Long>? = null,
    @SerializedName("mentionedMe")
    val mentionedMe: Boolean,
    val coords: CoordinatesDto? = null,
    val link: String? = null,
    val attachment: AttachmentDto? = null,
    val users: Map<String, UserPreviewDto>? = null
)

data class CoordinatesDto(
    val lat: Double,
    val long: Double
)

data class AttachmentDto(
    val url: String,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE, VIDEO, AUDIO
}

data class UserPreviewDto(
    val name: String,
    val avatar: String? = null
)