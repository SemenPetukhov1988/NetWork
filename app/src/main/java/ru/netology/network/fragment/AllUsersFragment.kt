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
import kotlinx.coroutines.launch
import ru.netology.adapter.UsersAdapter
import ru.netology.network.databinding.FragmentUsersBinding


import ru.netology.network.viewmodel.UsersViewModel


@AndroidEntryPoint
class UsersAllFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsersViewModel by viewModels()

    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем RecyclerView
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = UsersAdapter { user ->
                // Сюда потом вставишь логику перехода в профиль
                // Например: findNavController().navigate(...)
            }
        }

        adapter = binding.usersRecyclerView.adapter as UsersAdapter

        // Слушаем состояние из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    // Пока грузится — ничего не делаем, список остаётся пустым
                    return@collect
                }

                if (state.errorMessage != null) {
                    // Если ошибка — можно вывести Toast или просто оставить пустой список
                    // Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
                    return@collect
                }

                // Если данные пришли — показываем их
                adapter.submitList(state.users)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
