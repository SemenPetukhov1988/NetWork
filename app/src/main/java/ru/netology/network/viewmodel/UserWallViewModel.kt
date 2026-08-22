package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import ru.netology.network.api.UserWallApi
import ru.netology.network.datasource.UserWallPagingSource
import ru.netology.network.dto.response.PostDto

@HiltViewModel
class UserWallViewModel @Inject constructor(
    @Named("normal") private val userWallApi: UserWallApi
) : ViewModel() {

    fun getWallFlow(authorId: Long): Flow<androidx.paging.PagingData<PostDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { UserWallPagingSource(authorId, userWallApi, 20) }
        ).flow.cachedIn(viewModelScope)
}
