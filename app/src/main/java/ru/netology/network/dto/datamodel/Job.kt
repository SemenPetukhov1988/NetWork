package ru.netology.network.dto.datamodel

class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String, //какая то дата
    val finish: String, // какая то дата
    val link: String
)