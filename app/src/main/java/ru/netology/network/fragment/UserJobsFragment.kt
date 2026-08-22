package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.adapter.JobsAdapter
import ru.netology.network.databinding.FragmentUserJobsBinding
import ru.netology.network.viewmodel.UserJobsViewModel

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    // Подключаем нашу новую ViewModel через Hilt
    private val viewModel: UserJobsViewModel by viewModels()

    private lateinit var adapter: JobsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Настраиваем RecyclerView
        // ВАЖНО: используем postsRecyclerWall — тот самый ID, который у тебя уже работает в XML
        binding.userJobsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 2. Инициализируем адаптер
        adapter = JobsAdapter { job ->
            android.util.Log.d("JOBS_CLICK", "Нажали на работу: ${job.id}, компания: ${job.name}")
            // Сюда потом можно добавить переход на детальную страницу работы, если будет маршрут
        }

        binding.userJobsRecycler.adapter = adapter

        // 3. Получаем userId из аргументов (тот самый бандл, который ты передаёшь из OtherProfileFragment)
        val userIdString = arguments?.getString("userId")

        if (userIdString != null) {
            val userId = userIdString.toLongOrNull()

            if (userId != null) {
                // 4. Подписываемся на стейт из ViewModel и отдаём список в адаптер
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.uiState.collectLatest { state ->
                        // Можно добавить обработку ошибок через Toast, если захочешь позже
                        // if (state.errorMessage != null) { ... }

                        adapter.submitList(state.jobs)
                    }
                }

                // 5. Сразу запускаем загрузку списка, передавая нужный ID пользователя
                viewModel.loadJobs(userId)
            } else {
                android.util.Log.e("UserJobs", "Не удалось распарсить userId: $userIdString")
            }
        } else {
            android.util.Log.e("UserJobs", "В UserJobsFragment не передан аргумент userId!")
            // Тут можно показать пользователю ошибку, если нужно
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
