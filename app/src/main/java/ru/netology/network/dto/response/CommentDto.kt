package ru.netology.network.dto.response

import com.google.gson.annotations.SerializedName

data class CommentDto(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Long>,
    @SerializedName("likedByMe")
    val likedByMe: Boolean
)
