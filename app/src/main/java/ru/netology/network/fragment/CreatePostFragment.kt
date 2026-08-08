package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.databinding.FragmentCreatePostBinding
import ru.netology.network.viewmodel.CreatePostViewModel

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    // ViewModel привязана к Activity, поэтому живёт дольше фрагмента
    private val viewModel: CreatePostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отступы от системных панелей (статус-бар, навигация)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        setupClickListeners()
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // 1. Состояние загрузки
                if (state.isLoading) {
                    binding.ivCheckMark.isEnabled = false
                    return@collect
                } else {
                    binding.ivCheckMark.isEnabled = true
                }

                // 2. Успех: пост создан
                if (state.post != null) {
                    Toast.makeText(requireContext(), "Пост опубликован!", Toast.LENGTH_SHORT).show()

                    // ВАЖНО: сбрасываем состояние, чтобы при повторном входе
                    // фрагмент не считал, что пост уже опубликован
                    viewModel.clearPostState()

                    findNavController().popBackStack()
                    clearForm()
                    return@collect
                }

                // 3. Ошибка
                if (!state.errorMessage.isNullOrBlank()) {
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivCheckMark.setOnClickListener {
            val content = binding.etPostText.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст поста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Пока без картинки (для первого теста)
            val imageFile: java.io.File? = null
            val latitude: Double? = null
            val longitude: Double? = null
            val link: String? = null

            viewModel.createPost(
                content = content,
                imageFile = imageFile,
                latitude = latitude,
                longitude = longitude,
                link = link
            )
        }
    }

    private fun clearForm() {
        binding.etPostText.text?.clear()
        // Если позже добавишь другие поля (ссылка, место и т.д.) — очисти их здесь
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
