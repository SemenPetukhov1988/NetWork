package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.network.dto.response.EventDto

interface EventsApi {

    // --- БАЗОВЫЕ ОПЕРАЦИИ ---

    /** GET /api/events - Получить список событий (основная лента) */
    @GET("/api/events")
    suspend fun getEvents(): List<EventDto>

    /** POST /api/events - Создать событие */
    @POST("/api/events")
    suspend fun createEvent(@Body event: EventDto): EventDto

    /** DELETE /api/events/{id} - Удалить событие */
    @DELETE("/api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Unit

    /** GET /api/events/{id} - Получить одно событие по ID */
    @GET("/api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): EventDto

    /** GET /api/events/latest - Получить последние события (отдельный эндпоинт) */
    @GET("/api/events/latest")
    suspend fun getLatestEvents(): List<EventDto>

    // --- ПАГИНАЦИЯ (очень важно для ленты!) ---
    // Вместо обычной нумерации страниц (page=1), тут используется подход "до/после/новее"

    /** GET /api/events/{id}/newer - События, созданные ПОСЛЕ указанного */
    @GET("/api/events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Long): List<EventDto>

    /** GET /api/events/{id}/before - События, созданные ДО указанного */
    @GET("/api/events/{id}/before")
    suspend fun getBeforeEvents(@Path("id") id: Long): List<EventDto>

    /** GET /api/events/{id}/after - События, созданные ПОСЛЕ (аналог newer, проверь логику бэкенда) */
    @GET("/api/events/{id}/after")
    suspend fun getAfterEvents(@Path("id") id: Long): List<EventDto>

    // --- ВЗАИМОДЕЙСТВИЯ (Лайки, Участники) ---

    /** POST /api/events/{id}/likes - Поставить лайк */
    @POST("/api/events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Unit

    /** DELETE /api/events/{id}/likes - Убрать лайк */
    @DELETE("/api/events/{id}/likes")
    suspend fun unlikeEvent(@Path("id") id: Long): Unit

    /** POST /api/events/{id}/participants - Стать участником */
    @POST("/api/events/{id}/participants")
    suspend fun joinEvent(@Path("id") id: Long): Unit

    /** DELETE /api/events/{id}/participants - Выйти из участников */
    @DELETE("/api/events/{id}/participants")
    suspend fun leaveEvent(@Path("id") id: Long): Unit
}
