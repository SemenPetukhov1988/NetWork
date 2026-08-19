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

    // Нам НЕ нужен LocalAuthRepository, потому что это чужой профиль

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

        // --- 1. Отступы под статус-бар (оставляем как было) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Получаем userId из аргументов. Это ключевой момент!
        val userId = arguments?.getString("userId")
        if (userId == null) {
            Log.e("OtherProfileFragment", "❌ Не передан userId! Нужно передавать при навигации.")
            // Тут можно показать Toast или закрыть фрагмент, но для UI пока просто логируем
        } else {
            Log.d("OtherProfileFragment", "✅ Получен userId для чужого профиля: $userId")

            // Сюда потом добавим загрузку имени в tvHeaderName
            // binding.tvHeaderName.text = "Имя пользователя (заглушка)"
        }

        // --- 2. НАСТРОЙКА VIEWPAGER2 И ТАБОВ ---
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                // Передаём userId во вкладки через arguments
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

        // --- 3. Кнопка «Назад» ---
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Кнопку «Выход» мы убрали из XML, поэтому здесь её нет

        // --- 4. ЗАГРУЗКА АВАТАРКИ (ЗАГЛУШКА ДЛЯ UI) ---
        loadUserAvatar()
    }

    private fun loadUserAvatar() {
        // Пока НЕ делаем запрос к серверу, чтобы не усложнять.
        // Просто показываем дефолтную аватарку, чтобы UI не был пустым.

        val imageView = binding.imgAvatar

        Glide.with(requireContext())
            .load(R.drawable.avatar) // Пока заглушка
            .placeholder(R.drawable.load)
            .error(R.drawable.smile)
            .into(imageView)

        Log.d("OtherProfileFragment", "📷 Аватарка установлена (заглушка)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
