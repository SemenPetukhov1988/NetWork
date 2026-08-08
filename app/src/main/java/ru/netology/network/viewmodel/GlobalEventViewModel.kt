package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import ru.netology.network.PagingSource.GlobalEventPagingSource
import ru.netology.network.api.EventsApi
import ru.netology.network.dto.response.EventDto

@HiltViewModel
class GlobalEventViewModel
@Inject constructor (@Named("normal") private val eventsApi: EventsApi
) : ViewModel() {

    val eventsFlow: Flow<PagingData<EventDto>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { GlobalEventPagingSource(eventsApi) }
        ).flow.cachedIn(viewModelScope)

}
