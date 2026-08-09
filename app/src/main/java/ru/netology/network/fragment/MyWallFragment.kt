package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.network.adapter.PostAdapter
import ru.netology.network.databinding.FragmentMyWallBinding
import ru.netology.network.viewmodel.MyWallViewModel

@AndroidEntryPoint
class MyWallFragment : Fragment() {

    private var _binding: FragmentMyWallBinding? = null
    private val binding get() = _binding!!

    // Используем MyWallViewModel — только для твоей ленты
    private val viewModel: MyWallViewModel by viewModels()

    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        binding.postsRecyclerWall.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Инициализация адаптера
        adapter = PostAdapter(
            onAuthorClick = { /* TODO: переход в профиль */ },
            onOptionsClick = { /* TODO: меню поста */ }
        )

        binding.postsRecyclerWall.adapter = adapter

        // Кнопка «Создать пост» — если нужна на этом экране
//        binding.fabAddPost?.setOnClickListener {
//            findNavController().navigate(ru.netology.network.R.id.createPostFragment)
//        }

        // Подписка на поток «моей ленты»
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myWallFlow.collectLatest { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
