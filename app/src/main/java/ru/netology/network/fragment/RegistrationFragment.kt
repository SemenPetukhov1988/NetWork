package ru.netology.network.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.R
import ru.netology.network.databinding.FragmentRegistrationBinding
import ru.netology.network.dto.statemodel.AuthUiState
import ru.netology.network.viewmodel.RegistrationViewModel


@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    // Hilt сам создаст ViewModel и подставит туда репозиторий
    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                // Добавляем отступ только сверху. Остальные оставляем как есть.
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

        viewModel.register(login, password, name)
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

        // Обработка ошибок
        if (!state.isSuccess && state.errorMessage != null) {
            Log.e("REGISTRATION_ERROR", state.errorMessage!!)
            Toast.makeText(requireContext(), state.errorMessage!!, Toast.LENGTH_LONG).show()
        }

        // Обработка успеха
        if (state.isSuccess) {
            val token = state.token
            if (token != null) {
                // ГЛАВНОЕ: ты видишь токен в Logcat!
                Log.d("REGISTRATION_SUCCESS", "🎉 ТОКЕН ПОЛУЧЕН: $token")
                Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.authFragment)
                // TODO: Завтра сюда вставишь сохранение токена
                // saveToken(token)
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