package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.network.R
import ru.netology.network.databinding.FragmentUserWallBinding

class UserWallFragment : Fragment() {
    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString("userId")
        Log.d("UserWallFragment", "📂 Загружаю ленту для пользователя: ${userId ?: "неизвестен"}")
        // Сюда потом добавишь логику загрузки ленты
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
