package ru.netology.network.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.network.api.MyWallApi
import ru.netology.network.dto.response.PostDto

class MyWallPagingSource(
    private val api: MyWallApi,
    private val initialCount: Int = 20
) : PagingSource<Long, PostDto>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, PostDto> =
        withContext(Dispatchers.IO) {
            try {
                val key = params.key

                if (key == null) {
                    // Первая загрузка: самые свежие посты из «моей ленты»
                    val posts = api.getMyLatest(count = initialCount)

                    if (posts.isEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }

                    LoadResult.Page(
                        data = posts,
                        prevKey = null,
                        nextKey = posts.last().id
                    )
                } else {
                    // Подгрузка старых постов (до указанного ID)
                    val posts = api.getMyBefore(postId = key, count = params.loadSize)

                    if (posts.isEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = key,
                            nextKey = null
                        )
                    }

                    LoadResult.Page(
                        data = posts,
                        prevKey = key,
                        nextKey = posts.last().id
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<Long, PostDto>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }
    }
}
