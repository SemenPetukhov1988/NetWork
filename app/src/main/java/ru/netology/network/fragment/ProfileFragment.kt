package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.api.UsersApi
import ru.netology.network.databinding.FragmentProfileBinding
import ru.netology.network.repository.LocalAuthRepository

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var localAuthRepository: LocalAuthRepository

    @Inject
    @field:Named("normal")
    lateinit var usersApi: UsersApi

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

        // --- 1. Настройка отступов под статус-бар ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // --- 2. НАСТРОЙКА VIEWPAGER2 И ТАБОВ (ИСПРАВЛЕНО) ---
        // Создаем адаптер для ViewPager2
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MyWallFragment()
                    else -> MyJobFragment()
                }
            }
        }

        binding.viewPagerWallJobs.adapter = viewPagerAdapter

        // Связываем Табы и ViewPager через Mediator
        // Это автоматически переключает страницы при клике на таб и наоборот
        TabLayoutMediator(binding.tabsWallJobs, binding.viewPagerWallJobs) { tab, position ->
            when (position) {
                0 -> tab.text = "Wall"
                1 -> tab.text = "Jobs"
            }
        }.attach()

        // --- 3. Кнопка «Назад» ---
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // --- 4. Кнопка «Выход» ---
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                localAuthRepository.clearToken()
                localAuthRepository.clearUserId()
                findNavController().navigate(R.id.action_profileFragment_to_authFragment)
                Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 5. ЗАГРУЗКА АВАТАРА ---
        loadUserAvatar()
    }

    private fun loadUserAvatar() {
        lifecycleScope.launch {
            try {
                val userId = localAuthRepository.getUserId()

                if (userId == null) {
                    Log.w("ProfileFragment", "⚠️ ID пользователя не найден!")
                    return@launch
                }

                Log.d("ProfileFragment", "✅ ID пользователя найден: \$userId")
                val user = usersApi.getUserById(userId)
                Log.d("ProfileFragment", "📡 Ответ от сервера получен")

                val imageView = binding.imgAvatar

                if (!user.avatar.isNullOrBlank()) {
                    Glide.with(requireContext())
                        .load(user.avatar)
                        .circleCrop()
                        .placeholder(R.drawable.load)
                        .error(R.drawable.smile)
                        .into(imageView)
                } else {
                    Log.d("ProfileFragment", "📷 На сервере нет аватара, ставим дефолт")
                    Glide.with(requireContext())
                        .load(R.drawable.avatar)
                        .circleCrop()
                        .into(imageView)
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "❌ Ошибка загрузки профиля: \${e.message}", e)
                Glide.with(requireContext())
                    .load(R.drawable.avatar)
                    .circleCrop()
                    .into(binding.imgAvatar)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
