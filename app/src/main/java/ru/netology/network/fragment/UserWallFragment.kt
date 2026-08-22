package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.network.adapter.PostAdapter
import ru.netology.network.databinding.FragmentUserWallBinding
import ru.netology.network.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : Fragment() {

    private var _binding: FragmentUserWallBinding? = null
    private val binding get() = _binding!!

    // Подтягиваем ViewModel
    private val viewModel: UserWallViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Настраиваем RecyclerView (тот самый postsRecyclerWall из твоего XML)
        binding.userWallPostsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 2. Получаем authorId из бандла (чтобы знать, чью стену грузить)
        val authorId = arguments?.getString("userId")?.toLongOrNull()
            ?: throw IllegalStateException("В UserWallFragment не передан userId")

        // 3. Создаём адаптер.
        // Клик по автору и по кнопке меню — пока просто заглушки, ничего не делают.
        adapter = PostAdapter(
            onAuthorClick = { post ->
                // TODO: сюда потом добавишь переход на профиль
                // А пока просто логируем, чтобы видеть в Logcat, что клик сработал
                android.util.Log.d("UserWall", "Клик по автору поста: ${post.author}")
            },
            onOptionsClick = { post ->
                // TODO: сюда потом добавишь меню (пожаловаться и т.п.)
                android.util.Log.d("UserWall", "Нажата кнопка меню у поста: ${post.id}")
            }
        )

        binding.userWallPostsRecycler.adapter = adapter

        // 4. Подписываемся на поток данных из ViewModel и отдаём их в адаптер
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWallFlow(authorId)
                .collectLatest { pagingData ->
                    adapter.submitData(lifecycle, pagingData)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
