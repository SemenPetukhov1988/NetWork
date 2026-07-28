package ru.netology.network.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.databinding.FragmentMyWallBinding
import ru.netology.network.repository.LocalAuthRepository

@AndroidEntryPoint
class MyWallFragment : Fragment(R.layout.fragment_my_wall) {

    private var _binding: FragmentMyWallBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var localAuthRepository: LocalAuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWallBinding.inflate(inflater, container, false)

        // ✅ ВЕШАЕМ СЛУШАТЕЛЬ ПРЯМО ЗДЕСЬ — это гарантирует, что он будет один
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                localAuthRepository.clearToken()
                findNavController().navigate(R.id.action_myWallFragment_to_authFragment)
            }
        }

        return binding.root
    }

    // onViewCreated теперь вообще не нужен для кликов
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Тут оставь только инициализацию списков, адаптеров и т.д., если есть
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}