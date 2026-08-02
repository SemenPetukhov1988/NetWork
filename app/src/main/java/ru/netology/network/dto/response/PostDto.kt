package ru.netology.network.dto.response



data class PostDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: CoordsDto?,
    val link: String?,
    val mentionIds: List<Long>,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Long>,
    val likedByMe: Boolean,
    val attachment: AttachmentDto?
)

data class CoordsDto(
    val lat: Double,
    val long: Double
)

data class AttachmentDto(
    val url: String,
    val type: String
)