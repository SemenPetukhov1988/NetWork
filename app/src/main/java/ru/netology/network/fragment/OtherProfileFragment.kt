package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.netology.network.databinding.FragmentOtherProfileBinding

@AndroidEntryPoint
class OtherProfileFragment : Fragment() {

    private var _binding: FragmentOtherProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    @field:Named("normal")
    lateinit var usersApi: UsersApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. Отступы под статус-бар ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // --- 2. Достаём данные из бандла ---
        val userId = arguments?.getString("userId")
        val userName = arguments?.getString("userName")
        val userAvatar = arguments?.getString("userAvatar")

        if (userId.isNullOrBlank()) {
            Log.e("OtherProfileFragment", "❌ Не передан userId! Нужно передавать при навигации.")
        } else {
            Log.d("OtherProfileFragment", "✅ Получен userId: $userId")

            // Сразу ставим имя, если оно пришло
            userName?.let {
                binding.tvHeaderName.text = it
                Log.d("OtherProfileFragment", "👤 Имя: $it")
            } ?: run {
                binding.tvHeaderName.text = "Имя не указано"
            }
        }

        // Загружаем аватарку из бандла (если есть)
        loadUserAvatar(userAvatar)

        // --- 3. НАСТРОЙКА VIEWPAGER2 И ТАБОВ ---
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> UserWallFragment().apply {
                        arguments = Bundle().apply { putString("userId", userId) }
                    }
                    else -> UserJobsFragment().apply {
                        arguments = Bundle().apply { putString("userId", userId) }
                    }
                }
            }
        }

        binding.viewPagerWallJobs.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabsWallJobs, binding.viewPagerWallJobs) { tab, position ->
            when (position) {
                0 -> tab.text = "Wall"
                1 -> tab.text = "Jobs"
            }
        }.attach()

        // --- 4. Кнопка «Назад» ---
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // Обновлённый метод: принимает ссылку и решает, что грузить
    private fun loadUserAvatar(avatarUrl: String?) {
        val imageView = binding.imgAvatar

        // Ссылка для теста (та же самая)


        if (!avatarUrl.isNullOrBlank()) {
            // Случай: аватарка есть
            Glide.with(requireContext())
                .load(avatarUrl)   // Для теста грузим смайл
                .placeholder(R.drawable.load) // Обязательно показываем, что грузится
                .error(R.drawable.smile)      // Если ошибка — тоже смайл
                .fitCenter()                  // Важно: чтобы маленький смайл влез в шапку
                .into(imageView)

            Log.d("OtherProfileFragment", "📷 Ветка IF: грузим локальный смайл")
        } else {
            // Случай: аватарки нет
            Glide.with(requireContext())
                .load(R.drawable.avatar)                   // Грузим ту же ссылку, что и раньше
                .placeholder(R.drawable.load) // <--- ВОТ ЭТОГО НЕ ХВАТАЛО! Покажи иконку загрузки
                .error(R.drawable.smile)      // Если ссылка битая — покажи смайл
                .fitCenter()                  // Чтобы картинка не уехала за экран
                .into(imageView)

            Log.d("OtherProfileFragment", "📷 Ветка ELSE: грузим ссылку с плейсхолдером")
        }
    }
}
