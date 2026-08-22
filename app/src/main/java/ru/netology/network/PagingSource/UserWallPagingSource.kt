package ru.netology.network.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.network.api.UserWallApi
import ru.netology.network.dto.response.PostDto

class UserWallPagingSource(
    private val authorId: Long,
    private val api: UserWallApi,
    private val initialCount: Int = 20
) : PagingSource<Long, PostDto>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, PostDto> =
        withContext(Dispatchers.IO) {
            try {
                val key = params.key

                // 1. Обработка ПЕРВОЙ загрузки (нет ключа)
                if (key == null) {
                    // Используем базовый эндпоинт.
                    // Если бэк требует latest, раскомментируй строку ниже и закомментируй getUserWall
                    // val posts = api.getUserLatest(authorId = authorId, count = initialCount)
                    val posts = api.getUserWall(authorId = authorId)

                    if (posts.isEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }

                    return@withContext LoadResult.Page(
                        data = posts,
                        // Для первой пачки:
                        // prevKey (скролл вверх) - это самый старый пост (первый в списке)
                        // nextKey (скролл вниз) - это самый новый пост (последний в списке)
                        prevKey = posts.first().id,
                        nextKey = posts.last().id
                    )
                }

                // 2. Обработка ПОДГРУЗКИ (есть ключ)
                // Теперь мы явно проверяем тип params через when
                return@withContext when (params) {
                    is LoadParams.Append -> {
                        // Скролл ВНИЗ: нужны посты НОВЕЕ, чем key
                        val posts = api.getUserNewer(
                            authorId = authorId,
                            postId = key
                        )

                        if (posts.isEmpty()) {
                            return@withContext LoadResult.Page(
                                data = emptyList(),
                                prevKey = key,
                                nextKey = null
                            )
                        }

                        LoadResult.Page(
                            data = posts,
                            prevKey = key, // Предыдущая граница - это тот ключ, с которого начали
                            nextKey = posts.last().id // Новая граница - последний полученный пост
                        )
                    }

                    is LoadParams.Prepend -> {
                        // Скролл ВВЕРХ: нужны посты СТАРЕЕ, чем key
                        val posts = api.getUserBefore(
                            authorId = authorId,
                            postId = key,
                            count = params.loadSize
                        )

                        if (posts.isEmpty()) {
                            return@withContext LoadResult.Page(
                                data = emptyList(),
                                prevKey = null,
                                nextKey = key
                            )
                        }

                        LoadResult.Page(
                            data = posts,
                            prevKey = posts.first().id, // Новая граница - первый полученный пост
                            nextKey = key // Предыдущая граница - это тот ключ, с которого начали
                        )
                    }

                    // LoadParams.Refresh обрабатывается отдельно через getRefreshKey,
                    // но чтобы when был exhaustive (исчерпывающим), добавим заглушку.
                    // На практике сюда код не дойдет, если логика верна.
                    is LoadParams.Refresh -> {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<Long, PostDto>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }
    }
}
