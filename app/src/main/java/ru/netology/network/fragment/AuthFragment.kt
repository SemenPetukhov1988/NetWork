package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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


    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel() // Подписываемся на изменения стейта
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
        // Забираем данные из полей
        val login = binding.loginEditText.text.toString().trim()
        val pass = binding.passwordEditText.text.toString()

        // Простая проверка пустоты (валидация UI)
        if (login.isEmpty()) {
            showFieldError(binding.loginErrorText, "Введите логин")
            return
        }
        if (pass.isEmpty()) {
            showFieldError(binding.passwordErrorText, "Введите пароль")
            return
        }

        clearErrors()

        // Вызов функции во ViewModel. Фрагмент НЕ знает про Repository.
        viewModel.login(login, pass)
    }

    // Функция наблюдения за состоянием от ViewModel
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle защищает от утечек при поворотах экрана
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    // Отрисовка интерфейса в зависимости от того, что прислал VM
    private fun renderState(state: AuthUiState) {
        setLoading(state.isLoading)

        if (state.errorMessage != null) {
            Log.e("AUTH_ERROR", state.errorMessage)
            Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
        }

        if (state.isSuccess) {
            Log.d("AUTH_SUCCESS", "Переход к ленте!")
            Toast.makeText(requireContext(), "Успешный вход!", Toast.LENGTH_SHORT).show()
            // TODO: Здесь будет навигация
            // findNavController().navigate(R.id.action_authFragment_to_feedFragment)
        }
    }

    // --- Вспомогательные функции работы с UI ---

    private fun setLoading(isLoading: Boolean) {
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