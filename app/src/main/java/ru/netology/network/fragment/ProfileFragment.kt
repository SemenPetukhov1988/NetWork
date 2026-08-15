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
import com.bumptech.glide.Glide
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

    // ВАЖНО: Добавляем UsersApi с тем же именем, что и в NetworkModule (@Named("normal"))
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

        // --- 2. Настройка вкладок (TabLayout) ---
        with(binding.tabsWallJobs) {
            addTab(newTab().setText("Моя стена"))
            addTab(newTab().setText("Работа"))

            addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                    when (tab.position) {
                        0 -> switchChild(MyWallFragment())
                        else -> switchChild(MyJobFragment())
                    }
                }
                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            })
        }

        if (savedInstanceState == null) {
            switchChild(MyWallFragment())
        }

        // --- 3. Кнопка «Назад» ---
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // --- 4. Кнопка «Выход» (ИСПРАВЛЕНО: чистим и токен, и userId!) ---
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                localAuthRepository.clearToken()
                localAuthRepository.clearUserId() // <-- Критически важно!
                findNavController().navigate(R.id.action_profileFragment_to_authFragment)
                Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 5. ЗАГРУЗКА АВАТАРА (ГЛАВНОЕ ИСПРАВЛЕНИЕ) ---
        loadUserAvatar()
    }

    /**
     * Отдельная функция для загрузки аватара.
     * Так код чище, и его проще отлаживать.
     */
    private fun loadUserAvatar() {
        lifecycleScope.launch {
            try {
                // 1. Получаем ID пользователя из локального хранилища
                val userId = localAuthRepository.getUserId()

                if (userId == null) {
                    Log.w("ProfileFragment", "⚠️ ID пользователя не найден! Возможно, сессия истекла.")
                    // Если ID нет, оставляем дефолтную картинку (она должна быть в XML)
                    return@launch
                }

                Log.d("ProfileFragment", "✅ ID пользователя найден: $userId. Делаем запрос...")

                // 2. Запрос к API: GET /api/users/{id}
                // Токен автоматически подставится твоим AuthInterceptor из NetworkModule
                val user = usersApi.getUserById(userId)

                Log.d("ProfileFragment", "📡 Ответ от сервера получен. Аватар: ${user.avatar}")

                // 3. Загрузка картинки через Glide
                val imageView = binding.imgAvatar // <-- Убедись, что в XML у ImageView id="imgAvatar"

                if (!user.avatar.isNullOrBlank()) {
                    Glide.with(requireContext())
                        .load(user.avatar) // Это должна быть полная ссылка (http://...)
                        .circleCrop()
                        .placeholder(R.drawable.load) // Картинка-заглушка, пока грузится
                        .error(R.drawable.smile)      // Картинка при ошибке загрузки
                        .into(imageView)

                    Log.d("ProfileFragment", "📸 Аватар успешно загружен в ImageView")
                } else {
                    Log.d("ProfileFragment", "📷 На сервере нет аватара, ставим дефолт")
                    Glide.with(requireContext())
                        .load(R.drawable.avatar)
                        .circleCrop()
                        .into(imageView)
                }

            } catch (e: Exception) {
                // Сюда попадем, если сервер вернул ошибку (403, 500) или нет сети
                Log.e("ProfileFragment", "❌ Ошибка загрузки профиля: ${e.message}", e)

                // Даже при ошибке показываем дефолтную аватарку, чтобы UI не ломался
                Glide.with(requireContext())
                    .load(R.drawable.avatar)
                    .circleCrop()
                    .into(binding.imgAvatar)
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
