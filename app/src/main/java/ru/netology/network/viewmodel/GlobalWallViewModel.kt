package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import ru.netology.network.PagingSource.GlobalPostPagingSource
import ru.netology.network.api.GlobalWallApi
import javax.inject.Inject

@HiltViewModel
class WallViewModel @Inject constructor(
    @Named("normal") private val globalWallApi: GlobalWallApi
) : ViewModel() {

    val postsFlow: Flow<androidx.paging.PagingData<ru.netology.network.dto.response.PostDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { GlobalPostPagingSource(globalWallApi) }
        ).flow.cachedIn(viewModelScope)
}