package ru.netology.network.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

import ru.netology.network.R
import ru.netology.network.databinding.FragmentMainTabsBinding
import ru.netology.network.repository.LocalAuthRepository

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

        // 1. Отступы под статус-бар
        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        // 2. ГЛАВНЫЙ КОСТЫЛЬ: сразу показываем ленту.
        // Не смотрим ни на savedInstanceState, ни на историю.
        // Просто кладём в контейнер фрагмент ленты.
        switchTab(WallAllUsersFragment())

        // 3. Теперь вешаем слушатель на табы.
        // Он будет работать только для будущих кликов пользователя.
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_wall -> switchTab(WallAllUsersFragment())
                R.id.action_events -> switchTab(EventsAllUsersFragment())
                R.id.action_users -> switchTab(UsersAllFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        // Кнопка профиля
        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    // Простая замена фрагмента. Ничего лишнего.
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