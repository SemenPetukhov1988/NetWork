package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.R
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.network.adapter.PostAdapter
import ru.netology.network.databinding.FragmentWallAllUsersBinding
import ru.netology.network.viewmodel.WallViewModel

// ЭТА АННОТАЦИЯ ОБЯЗАТЕЛЬНА! Без неё Hilt не увидит фрагмент
@AndroidEntryPoint
class WallAllUsersFragment : Fragment() {

    private var _binding: FragmentWallAllUsersBinding? = null
    private val binding get() = _binding!!

    // ✅ ГЛАВНОЕ ИСПРАВЛЕНИЕ: Используем делегат, а НЕ @Inject
    // Hilt сам создаст ViewModel правильно, привязав её к жизненному циклу фрагмента
    private val viewModel: WallViewModel by viewModels()

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Создаем binding
        _binding = FragmentWallAllUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Настройка RecyclerView
        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // adapter пока не ставим, поставим ниже после создания
        }

        // Инициализация адаптера
        adapter = PostAdapter(
            //onLikeClick = { /* TODO: логика лайка */  },
           // onShareClick = { /* TODO: логика шаринга */ },
            onAuthorClick = { /* TODO: переход в профиль */ },
            onOptionsClick = { /* TODO: меню поста */ }
        )

        // Привязываем адаптер
        binding.postsRecyclerView.adapter = adapter

        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(ru.netology.network.R.id.createPostFragment)
        }
        // Подписка на поток данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) { // refresh — это загрузка первой страницы (самый главный кружок)
                    is LoadState.Loading -> {
                        // Показываем кружок (или ProgressBar)
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("WallAllUsers", "🔄 Грузим первую страницу постов...")
                    }
                    is LoadState.NotLoading -> {
                        // Скрываем кружок, когда всё загрузилось
                        binding.progressBar.visibility = View.GONE
                        Log.d("WallAllUsers", "✅ Данные загружены")
                    }
                    is LoadState.Error -> {
                        // Тут можно показать ошибку вместо кружка
                        binding.progressBar.visibility = View.GONE
                        Log.e("WallAllUsers", "❌ Ошибка загрузки ленты")
                        // Можно показать Toast или специальную View с ошибкой
                    }
                }

                // Если хочешь показывать кружок и при подгрузке следующих страниц (пагинация вниз)

            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // adapter можно не обнулять, сборщик мусора сам всё почистит
    }
}