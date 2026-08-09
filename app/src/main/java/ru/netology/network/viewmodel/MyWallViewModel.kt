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
import ru.netology.network.datasource.MyWallPagingSource
import ru.netology.network.api.GlobalWallApi
import ru.netology.network.api.MyWallApi
import javax.inject.Inject

@HiltViewModel
class MyWallViewModel @Inject constructor(
    @Named("normal") private val globalWallApi: GlobalWallApi,
    @Named("auth") private val myWallApi: MyWallApi  // <-- добавили инжект MyWallApi с тегом auth (токен уже полетит)
) : ViewModel() {

    // Твоя глобальная лента (оставляем как было)
    val postsFlow: Flow<androidx.paging.PagingData<ru.netology.network.dto.response.PostDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { GlobalPostPagingSource(globalWallApi) }
        ).flow.cachedIn(viewModelScope)

    // Твоя личная лента (новая)
    val myWallFlow: Flow<androidx.paging.PagingData<ru.netology.network.dto.response.PostDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MyWallPagingSource(myWallApi, 20) }
        ).flow.cachedIn(viewModelScope)
}
