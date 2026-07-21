package ru.netology.network.dto.datamodel

data class Comment(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likeOwnerIds: List<Long>,
    val likedByMe: Boolean
)