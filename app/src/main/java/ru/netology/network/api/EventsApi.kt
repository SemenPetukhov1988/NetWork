package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.EventDto

interface EventsApi {

    /** GET /api/events - Все события (глобальная лента) */
    @GET("/api/events")
    suspend fun getEvents(): List<EventDto>

    /** GET /api/events/latest - Последние события */
    @GET("/api/events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): List<EventDto>

    /** GET /api/events/{id} - Одно событие */
    @GET("/api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): EventDto

    /** POST /api/events - Создать событие */
    @POST("/api/events")
    suspend fun createEvent(@Body event: EventDto): EventDto

    /** DELETE /api/events/{id} - Удалить событие */
    @DELETE("/api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Unit

    // --- ПАГИНАЦИЯ ---
    @GET("/api/events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Long): List<EventDto>

    @GET("/api/events/{id}/before")
    suspend fun getBeforeEvents(@Path("id") id: Long, @Query("count") count: Int): List<EventDto>

    @GET("/api/events/{id}/after")
    suspend fun getAfterEvents(@Path("id") id: Long, @Query("count") count: Int): List<EventDto>

    // --- ВЗАИМОДЕЙСТВИЯ ---
    @POST("/api/events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): EventDto

    @DELETE("/api/events/{id}/likes")
    suspend fun unlikeEvent(@Path("id") id: Long): EventDto

    @POST("/api/events/{id}/participants")
    suspend fun joinEvent(@Path("id") id: Long): EventDto

    @DELETE("/api/events/{id}/participants")
    suspend fun leaveEvent(@Path("id") id: Long): EventDto
}