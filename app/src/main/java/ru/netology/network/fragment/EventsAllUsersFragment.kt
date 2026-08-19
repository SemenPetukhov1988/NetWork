package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.network.adapter.EventAdapter
import ru.netology.network.databinding.FragmentEventsAllUsersBinding
import ru.netology.network.viewmodel.GlobalEventViewModel

@AndroidEntryPoint
class EventsAllUsersFragment : Fragment() {

    private var _binding: FragmentEventsAllUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlobalEventViewModel by viewModels()

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsAllUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        adapter = EventAdapter(
            onAuthorClick = { /* TODO: переход в профиль автора события */ },
            onOptionsClick = { /* TODO: меню действий с событием */ }
        )

        binding.eventsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) { // refresh — это загрузка первой страницы (самый главный кружок)
                    is LoadState.Loading -> {
                        // Показываем кружок (или ProgressBar)
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("WallAllUsers", "🔄 Грузим первую страницу постов...")
                    }
                    is LoadState.NotLoading -> {
                        // Скрываем кружок, когда всё загрузилось
                        binding.progressBar.visibility = View.GONE
                        Log.d("WallAllUsers", "✅ Данные загружены")
                    }
                    is LoadState.Error -> {
                        // Тут можно показать ошибку вместо кружка
                        binding.progressBar.visibility = View.GONE
                        Log.e("WallAllUsers", "❌ Ошибка загрузки ленты")
                        // Можно показать Toast или специальную View с ошибкой
                    }
                }

                // Если хочешь показывать кружок и при подгрузке следующих страниц (пагинация вниз)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
