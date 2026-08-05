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
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.databinding.FragmentProfileBinding
import ru.netology.network.repository.LocalAuthRepository

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var localAuthRepository: LocalAuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.topBarProfile) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Добавляем отступ только сверху. Остальные оставляем как есть.
            v.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
        // 1. Настраиваем вкладки
        with(binding.tabsWallJobs) {
            addTab(newTab().setText("Моя стена"))
            addTab(newTab().setText("Работа"))

            // При выборе переключаем фрагмент
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when(tab.position){
                        0 -> switchChild(MyWallFragment())
                        else -> switchChild(MyJobFragment())
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        // 2. Если это первый запуск — показываем ленту "Wall"
        if (savedInstanceState == null) {
            switchChild(MyWallFragment())
        }

        // 3. Обработчик кнопки «Назад»
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                // 1. Очищаем токен через твой репозиторий (это у тебя уже работает)
                localAuthRepository.clearToken()

                // 2. Переходим на экран входа — ИСПОЛЬЗУЕМ НОВЫЙ ID из nav_graph
                findNavController().navigate(R.id.action_profileFragment_to_authFragment)

                Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchChild(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerWallJob, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}