package ru.netology.network.dto.datamodel

import ru.netology.nework.dto.Attachment

class Post(
    val id: Long,
    authorId: Long,
    author: String,
    authorJob: String,
    authorAvatar: String,
    content: String,
    published: String,
    coords: Coordinates? = null,
    link: String,
    mentionIds: List<Long>, // список тех кто упомянул кого то в посте
    mentionedMe: Boolean,
    likeOwnerIds: List<Long>, // id тех кто поставил лайк наверно для какого то отображения
    attachment: Attachment,
)