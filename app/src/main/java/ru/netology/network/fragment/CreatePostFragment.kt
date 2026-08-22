package ru.netology.network.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import ru.netology.network.databinding.FragmentCreatePostBinding
import ru.netology.network.viewmodel.CreatePostViewModel

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePostViewModel by activityViewModels()

    // Храним выбранный файл до момента УСПЕШНОЙ публикации
    private var currentImageFile: File? = null

    private companion object {
        private const val REQUEST_CODE_IMAGE = 100
        private const val EXTRA_FILE_KEY = "com.github.dhaval2404.imagepicker.EXTRA_FILE"
    }

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

        // Отступы под статус-бар
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
            viewModel.uiState.collectLatest { state ->
                // 1. Блокируем кнопку публикации, пока идёт загрузка
                binding.ivCheckMark.isEnabled = !state.isLoading

                // 2. Показываем/скрываем центральный оверлей с прогресс-баром и текстом
                binding.loadingOverlay.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                // 3. Показываем/скрываем блок ошибки
                if (!state.errorMessage.isNullOrBlank()) {
                    binding.errorBlock.visibility = View.VISIBLE
                    binding.tvError.text = state.errorMessage
                    binding.btnRetry.isEnabled = state.canRetry
                } else {
                    binding.errorBlock.visibility = View.GONE
                }

                // 4. Успех: переход назад и очистка формы
                if (state.post != null) {
                    Toast.makeText(requireContext(), "Пост опубликован!", Toast.LENGTH_SHORT).show()
                    viewModel.clearPostState()
                    findNavController().popBackStack()
                    clearForm()
                    return@collectLatest
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCamera.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start(REQUEST_CODE_IMAGE)
        }

        // Кнопка «Опубликовать»
        binding.ivCheckMark.setOnClickListener {
            val content = binding.etPostText.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст поста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageFile = currentImageFile
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

            // ВАЖНО: НЕ обнуляем currentImageFile здесь!
            // Если отправка упадёт с ошибкой, пользователь нажмёт «Повторить»,
            // и нам нужно, чтобы картинка всё ещё была доступна.
        }

        // Кнопка «Повторить» при ошибке
        binding.btnRetry.setOnClickListener {
            val content = binding.etPostText.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Текст поста не должен быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // currentImageFile всё ещё хранит файл, потому что мы не обнулили его выше
            val imageFile = currentImageFile

            viewModel.createPost(
                content = content,
                imageFile = imageFile,
                latitude = null,
                longitude = null,
                link = null
            )
        }

        // Кнопка "Удалить фото"
        binding.btnRemoveImage.setOnClickListener {
            currentImageFile = null
            binding.ivPostImage.visibility = View.GONE
            binding.btnRemoveImage.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            var file: File? = null

            // 1. Пробуем получить файл от библиотеки
            val libraryFile = data?.getSerializableExtra(EXTRA_FILE_KEY) as? File
            if (libraryFile != null && libraryFile.exists()) {
                file = libraryFile
            }

            // 2. Если не получилось — копируем из URI вручную
            if (file == null || !file.exists()) {
                val uri = data?.data
                if (uri != null) {
                    try {
                        file = copyUriToFile(uri, requireContext())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Ошибка обработки картинки: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }

            // 3. Финальная проверка и отображение
            if (file != null && file.exists()) {
                currentImageFile = file

                binding.ivPostImage.visibility = View.VISIBLE

                Glide.with(this)
                    .load(file)
                    .into(binding.ivPostImage)

                Toast.makeText(context, "Картинка выбрана: ${file.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось получить файл. Попробуйте другую картинку.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Надёжно копирует изображение из Uri во временную папку приложения.
     */
    private fun copyUriToFile(uri: Uri, context: android.content.Context): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, System.currentTimeMillis().toString() + ".jpg")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        if (!file.exists()) {
            throw IllegalStateException("Не удалось создать файл из URI")
        }
        return file
    }

    private fun clearForm() {
        binding.etPostText.text?.clear()
        binding.ivPostImage.visibility = View.GONE
        binding.btnRemoveImage.visibility = View.GONE
        // Очищаем файл ТОЛЬКО после успешной публикации
        currentImageFile = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
