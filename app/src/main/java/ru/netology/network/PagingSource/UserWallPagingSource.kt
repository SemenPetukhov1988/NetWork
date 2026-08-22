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

                // 1. ПЕРВАЯ ЗАГРУЗКА
                if (key == null) {
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
                        // prevKey - самый старый пост (начало ленты)
                        // nextKey - самый свежий пост (конец ленты)
                        prevKey = posts.last().id,
                        nextKey = posts.first().id
                    )
                }

                // 2. ПОДГРУЗКА ДАННЫХ
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
                                nextKey = null // Дальше вниз грузить нечего
                            )
                        }

                        LoadResult.Page(
                            data = posts,
                            prevKey = key,           // Предыдущая граница
                            nextKey = posts.first().id // Самый свежий из новых
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
                            // Если постов старше нет - значит, мы дошли до самого начала
                            return@withContext LoadResult.Page(
                                data = emptyList(),
                                prevKey = null,      // Дальше вверх грузить некуда
                                nextKey = key        // Но вниз можно вернуться к уже загруженному
                            )
                        }

                        LoadResult.Page(
                            data = posts,
                            prevKey = posts.last().id,  // Самый старый из полученных (новая граница вверх)
                            nextKey = key              // Предыдущая граница вниз
                        )
                    }

                    is LoadParams.Refresh -> {
                        // При рефреше мы просто запрашиваем первую пачку заново
                        val posts = api.getUserWall(authorId = authorId)

                        if (posts.isEmpty()) {
                            return@withContext LoadResult.Page(
                                data = emptyList(),
                                prevKey = null,
                                nextKey = null
                            )
                        }

                        LoadResult.Page(
                            data = posts,
                            prevKey = posts.last().id,
                            nextKey = posts.first().id
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
