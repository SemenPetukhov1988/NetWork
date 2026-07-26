package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.databinding.FragmentAuthBinding // ВАЖНО: подключи правильный Binding
import ru.netology.network.repository.AuthRepository

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authRepository: AuthRepository // Инжектим репозиторий через Hilt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Позже здесь инициализируем ViewModel через viewModels()
        // val viewModel: AuthViewModel by viewModels()
        // observeViewModel(viewModel)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.registerButton.setOnClickListener { attemptRegister() }
    }

    private fun attemptLogin() {
        // 1. Забираем данные
        val login = binding.loginEditText.text.toString().trim()
        val pass = binding.passwordEditText.text.toString()

        // 2. Простая клиентская валидация (заглушка требований ТЗ)
        if (login.isEmpty()) {
            showFieldError(binding.loginErrorText, "Поле не может быть пустым")
            return
        }
        if (pass.isEmpty()) {
            showFieldError(binding.passwordErrorText, "Поле не может быть пустым")
            return
        }

        clearErrors()
        setLoading(true) // Показываем ProgressBar

        // 3. Вызов РЕАЛЬНОГО API через Repository
        lifecycleScope.launch {
            try {
                //Здесь вызывается твой голый Retrofit-интерфейс!
                val response = authRepository.login(login, pass)

                // 4. Заглушка успеха: просто пишем в логи и имитируем переход
                Log.d("AUTH_SUCCESS", "Токен получен: ${response.token}")
                Toast.makeText(requireContext(), "Успешный вход!", Toast.LENGTH_SHORT).show()

                // TODO: Вместо Toast здесь будет навигация в MainFeedFragment
                // findNavController().navigate(R.id.action_authFragment_to_feedFragment)

            } catch (e: Exception) {
                // 5. Заглушка ошибки сервера
                Log.e("AUTH_ERROR", e.message ?: "Unknown error")
                Toast.makeText(requireContext(), "Ошибка входа: ${e.message}", Toast.LENGTH_LONG).show()
                // TODO: Если ошибка 400 - подсвечиваем красным поле пароля
            } finally {
                setLoading(false) // Всегда прячем лоадер
            }
        }
    }

    private fun attemptRegister() {
        // Логика аналогична Login, но вызывает repository.register(...)
        // Пока можно просто сделать Toast "Переход на регистрацию"
        Toast.makeText(requireContext(), "Функционал регистрации временно недоступен", Toast.LENGTH_SHORT).show()
        // TODO: Либо реализовать полную форму регистрации здесь же
    }

    // --- Вспомогательные функции-заглушки ---

    private fun setLoading(isLoading: Boolean) {
        binding.progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Блокируем ввод данных во время запроса
        binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loginEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
    }

    private fun showFieldError(textView: TextView?, message: String) {
        textView?.text = message
        textView?.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        binding.loginErrorText.visibility = View.GONE
        binding.passwordErrorText.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}