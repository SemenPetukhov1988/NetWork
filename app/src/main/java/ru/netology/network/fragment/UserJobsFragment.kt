package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.network.databinding.FragmentUserJobsBinding

class UserJobsFragment : Fragment() {

    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

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

        // Получаем userId из аргументов (он передаётся из OtherProfileFragment)
        val userId = arguments?.getString("userId")
        if (userId != null) {
            // Тут позже будет логика загрузки работ именно этого пользователя
            // Например: viewModel.loadJobs(userId)
        } else {
            // Можно вывести лог или Toast, если забыли передать userId
        }

        // Сюда потом подключишь адаптер:
        // val recyclerView = binding.userJobsRecycler
        // recyclerView.adapter = userJobsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
