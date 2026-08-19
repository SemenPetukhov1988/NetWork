package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.adapter.UsersAdapter
import ru.netology.network.R
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
                // Отправляем ВСЕ нужные данные сразу, чтобы не дёргать сервер ради имени и аватарки
                val bundle = bundleOf(
                    "userId" to user.id.toString(),
                    "userName" to user.name,
                    "userAvatar" to user.avatar
                )

                findNavController().navigate(
                    R.id.otherProfileFragment,
                    bundle
                )
            }
        }

        adapter = binding.usersRecyclerView.adapter as UsersAdapter

        // Слушаем состояние из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // 1. Показываем/скрываем кружок в зависимости от флага isLoading
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                // Дальше твоя старая логика: если не загрузка и нет ошибки — показываем список
                if (state.isLoading || state.errorMessage != null) return@collect

                adapter.submitList(state.users)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
