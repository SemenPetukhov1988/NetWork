package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels // Импорт для делегата
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle // Для безопасного сбора Flow
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import ru.netology.network.R
import ru.netology.network.databinding.FragmentAuthBinding
import ru.netology.network.dto.statemodel.AuthUiState

import ru.netology.network.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    // ViewModel уже подключена через Hilt
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Добавляем отступ сверху на высоту статус-бара, если это нужно
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                // Если внизу тоже есть панель навигации и она перекрывает контент — раскомментируй:
                // bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }
    }

    private fun attemptLogin() {
        val login = binding.loginEditText.text.toString().trim()
        val pass = binding.passwordEditText.text.toString()

        if (login.isEmpty()) {
            showFieldError(binding.loginErrorText, "Введите логин")
            return
        }
        if (pass.isEmpty()) {
            showFieldError(binding.passwordErrorText, "Введите пароль")
            return
        }

        clearErrors()

        // Отправляем данные во ViewModel.
        // Логика сохранения токена УЖЕ внутри viewModel.login()
        viewModel.login(login, pass)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: AuthUiState) {
        setLoading(state.isLoading)

        // 1. Обработка ошибки
        if (!state.isSuccess && state.errorMessage != null) {
            Log.e("AUTH_ERROR", state.errorMessage)
            Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
        }

        // 2. Обработка успеха
        if (state.isSuccess) {
            Log.d("AUTH_SUCCESS", "Переход к ленте!")

            // ВАЖНО: Делаем навигацию только один раз.
            // Если у тебя в AuthUiState флаг isSuccess не сбрасывается автоматически,
            // то этот блок сработает много раз подряд.
            // Самый простой способ для диплома — сделать переход и сразу сбросить флаг в ViewModel,
            // либо использовать одноразовый Event.

            // Пока сделаем так: переходим и показываем Toast.
            // Чтобы не было проблем с повторными переходами, можно проверить, не на том ли мы экране уже.
            // Но самый надежный вариант для твоего текущего кода — просто перейти.

            findNavController().navigate(R.id.action_global_to_feed)
            viewModel.resetState()

            // Опционально: если хочешь убрать Toast или состояние после перехода,
            // можно вызвать viewModel.resetState(), но это требует доп. метода в VM.
            // Для начала хватит этого.
        }
    }

    private fun setLoading(isLoading: Boolean) {
        // Если идет загрузка, блокируем кнопки и показываем индикатор
        binding.progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
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