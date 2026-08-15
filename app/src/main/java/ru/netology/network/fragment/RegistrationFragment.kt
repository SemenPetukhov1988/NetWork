package ru.netology.network.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.R
import ru.netology.network.databinding.FragmentRegistrationBinding
import ru.netology.network.dto.statemodel.AuthUiState
import ru.netology.network.viewmodel.RegistrationViewModel
import java.io.File

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    // Hilt подставит сюда нужный репозиторий
    private val viewModel: RegistrationViewModel by viewModels()

    // Сюда сохраняем файл картинки, чтобы потом отправить его в репозиторий
    private var selectedAvatarFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Твой старый код для отступов (чтобы статус бар не наезжал)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            attemptRegister()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Клик по карточке → открываем галерею
        binding.avatarCard.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }

    // Контракт для выбора картинки (современный способ вместо старого startActivityForResult)
    private val pickImageContract = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    /**
     * Обрабатываем выбор картинки
     */
    private fun onImageSelected(uri: Uri) {
        try {
            // 1. Превращаем URI из галереи в реальный файл.
            // Это обязательно: Retrofit умеет отправлять только файлы, а не ссылки content://
            val file = uriToFile(uri, requireContext())

            // 2. Сохраняем файл в переменную, чтобы передать его при регистрации
            selectedAvatarFile = file

            // 3. Находим ImageView по новому ID, который ты добавил в XML
            val imageView = binding.avatarImage

            // Загружаем картинку через Glide
            Glide.with(this)
                .load(file)
                .circleCrop() // Делаем аватарку круглой
                .into(imageView)

            Log.d("AVATAR_PICK", "Картинка выбрана! Файл сохранён: $file")
            Toast.makeText(requireContext(), "Фото выбрано!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("AVATAR_ERROR", "Не удалось обработать фото", e)
            Toast.makeText(requireContext(), "Ошибка при обработке фото", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Превращает URI в настоящий файл во временной папке приложения.
     * Работает даже на Android 10+ и выше, где нет прямого пути к файлу.
     */
    private fun uriToFile(uri: Uri, context: android.content.Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        // Создаём временный файл в кэше приложения
        val tempFile = File.createTempFile("avatar_", ".jpg", context.cacheDir)

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw Exception("Не удалось прочитать поток из URI")

        return tempFile
    }

    private fun attemptRegister() {
        val login = binding.loginEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val name = binding.nameEditText.text.toString().trim()

        if (login.isEmpty()) {
            Toast.makeText(requireContext(), "Введите логин", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Введите пароль", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
            return
        }

        // Передаем файл (или null, если не выбирали) в ViewModel
        viewModel.register(login, password, name, selectedAvatarFile)
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: AuthUiState) {
        setLoading(state.isLoading)

        if (!state.isSuccess && state.errorMessage != null) {
            Log.e("REGISTRATION_ERROR", state.errorMessage!!)
            Toast.makeText(requireContext(), state.errorMessage!!, Toast.LENGTH_LONG).show()
        }

        if (state.isSuccess) {
            val token = state.token
            if (token != null) {
                Log.d("REGISTRATION_SUCCESS", "🎉 ТОКЕН ПОЛУЧЕН: $token")
                Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.authFragment)
            } else {
                Log.w("REGISTRATION_SUCCESS", "Успех, но токен пустой")
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loginButton.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
