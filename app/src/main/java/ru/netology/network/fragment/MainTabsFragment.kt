package ru.netology.network.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

import ru.netology.network.R
import ru.netology.network.databinding.FragmentMainTabsBinding
import ru.netology.network.repository.LocalAuthRepository
import kotlin.jvm.java

@AndroidEntryPoint
class MainTabsFragment : Fragment() {

    private var _binding: FragmentMainTabsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var localAuthRepository: LocalAuthRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Настраиваем обработчик нажатий на меню (BottomNavigationView)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_wall -> {
                    // Передаем ЭКЗЕМПЛЯР фрагмента, а не класс!
                    switchTab(WallAllUsersFragment())
                    true
                }
                R.id.action_events -> {
                    switchTab(EventsAllUsersFragment())
                    true
                }
                R.id.action_users -> {
                    switchTab(UsersFragment())
                    true
                }
                else -> false
            }
        }

        // 2. Если это первый запуск (без восстановления состояния) — показываем ленту по умолчанию
        if (savedInstanceState == null) {
            switchTab(WallAllUsersFragment())
        }

        // 3. Кнопка выхода
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                // 1. Очищаем токен через твой репозиторий (это у тебя уже работает)
                localAuthRepository.clearToken()

                // 2. Переходим на экран входа — ИСПОЛЬЗУЕМ НОВЫЙ ID из nav_graph
                findNavController().navigate(R.id.action_feedFragment_to_authFragment)

                Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Функция для переключения вкладок.
     * Используем childFragmentManager, так как мы внутри фрагмента.
     * @param fragment Фрагмент, который нужно показать в контейнере.
     */
    private fun switchTab(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.tabsContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
