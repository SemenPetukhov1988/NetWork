package ru.netology.network.PagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.network.api.EventsApi
import ru.netology.network.dto.response.EventDto

class GlobalEventPagingSource(
    private val api: EventsApi,
    private val initialCount: Int = 20
) : PagingSource<Long, EventDto>() {


    override fun getRefreshKey(state: PagingState<Long, EventDto>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }
    }


    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, EventDto> =
        withContext(Dispatchers.IO) {
            try {
                val key = params.key

                if (key == null) {
                    // Первая загрузка: самые свежие события
                    val events = api.getLatestEvents(count = initialCount)

                    if (events.isEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }

                    // Ключ для следующей подгрузки — ID последнего загруженного элемента
                    LoadResult.Page(
                        data = events,
                        prevKey = null,
                        nextKey = events.last().id
                    )
                } else {
                    // Подгрузка более старых событий (те, что были ДО указанного ID)
                    val events = api.getBeforeEvents(id = key, count = params.loadSize)

                    if (events.isEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = key,
                            nextKey = null
                        )
                    }

                    // Ключ для следующей итерации — ID последнего элемента в текущем чанке
                    LoadResult.Page(
                        data = events,
                        prevKey = key,
                        nextKey = events.last().id
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }
        }

}