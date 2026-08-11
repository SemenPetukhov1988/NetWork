package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.adapter.JobsAdapter
import ru.netology.network.R

import ru.netology.network.databinding.FragmentMyJobBinding
import ru.netology.network.viewmodel.MyJobViewModel

@AndroidEntryPoint
class MyJobFragment : Fragment() {

    private var _binding: FragmentMyJobBinding? = null
    private val binding get() = _binding!!

    // Подключаем MyJobViewModel через Hilt
    private val viewModel: MyJobViewModel by viewModels()

    private lateinit var adapter: JobsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Настраиваем RecyclerView
        binding.postsRecyclerJob.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Если хочешь, чтобы не было дерганий при добавлении — можно добавить:
            // setHasFixedSize(true)
        }

        // 2. Инициализируем адаптер
        adapter = JobsAdapter { job ->
            android.util.Log.d("JOBS_CLICK", "Нажали на работу: ${job.id}, компания: ${job.name}")
            // Пример будущей навигации (закомментировано, чтобы не ломать, если маршрута нет):
            // findNavController().navigate(R.id.action_myJobFragment_to_jobDetailsFragment)
        }


        binding.postsRecyclerJob.adapter = adapter

        // 3. Кнопка добавления (FAB) — перекидывает на экран создания
        binding.fabAddJob.setOnClickListener {
            findNavController().navigate(R.id.addWorkFragment)
        }

        // 4. Подписываемся на стейт из ViewModel и отдаём список в адаптер
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // Тут можно добавить обработку ошибок/успехов через Toast, если нужно
                // if (state.errorMessage != null) { Toast.makeText(...).show() }
                // if (state.successMessage != null) { Toast.makeText(...).show() }

                // Самое важное: обновляем список в адаптере
                adapter.submitList(state.jobs)
            }
        }

        // 5. Сразу запускаем загрузку списка при открытии экрана
        viewModel.loadJobs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
