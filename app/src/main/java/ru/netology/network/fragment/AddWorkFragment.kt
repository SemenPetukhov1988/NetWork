package ru.netology.network.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlinx.coroutines.launch
import ru.netology.network.databinding.FragmentAddWorkBinding
import ru.netology.network.viewmodel.MyJobViewModel

@AndroidEntryPoint
class AddWorkFragment : Fragment() {

    private var _binding: FragmentAddWorkBinding? = null
    private val binding get() = _binding!!

    // ViewModel уже настроена на получение строк в формате ISO
    private val viewModel: MyJobViewModel by viewModels()

    // Формат, который видит пользователь в поле ввода: 11.08.2026
    private val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Формат, который требует сервер (ISO 8601): 2026-08-11T00:00:00.000Z
    private val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Обработка системных отступов (статус бар и т.д.)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setupClickListeners()
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.successMessage != null) {
                    Toast.makeText(requireContext(), state.successMessage, Toast.LENGTH_LONG).show()
                    findNavController().popBackStack() // Возвращаемся назад после успеха
                }

                if (state.errorMessage != null) {
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBackArrow.setOnClickListener { findNavController().popBackStack() }

        // Клик по полям дат открывает календарь
        binding.etStartDate.setOnClickListener { showDatePicker(binding.etStartDate) }
        binding.etFinishDate.setOnClickListener { showDatePicker(binding.etFinishDate) }

        binding.ivCheckMark.setOnClickListener {
            val company = binding.etCompany.text.toString().trim()
            val position = binding.etPosition.text.toString().trim()

            val startText = binding.etStartDate.text.toString().trim()
            val finishText = binding.etFinishDate.text.toString().trim()

            // Простая валидация: название и должность обязательны, дата начала обязательна
            if (company.isEmpty() || position.isEmpty() || startText.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните название компании, должность и дату начала", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- ГЛАВНОЕ: Конвертация дат ---

            // 1. Дата начала (обязательная)
            val serverStart = try {
                // Парсим то, что ввел пользователь (например, "11.08.2026")
                val localDate = LocalDate.parse(startText, displayFormatter)
                // Добавляем время 00:00:00 и форматируем в ISO (например, "2026-08-11T00:00:00.000Z")
                localDate.atStartOfDay().format(serverFormatter)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Неверный формат даты начала", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Дата окончания (может быть пустой)
            val serverFinish = if (finishText.isEmpty()) {
                null
            } else {
                try {
                    val localDate = LocalDate.parse(finishText, displayFormatter)
                    localDate.atStartOfDay().format(serverFormatter)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Неверный формат даты окончания", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // ОТПРАВКА
            // Обратите внимание: мы НЕ передаем id. Только данные.
            viewModel.createJob(
                name = company,
                position = position,
                start = serverStart,       // Строка в формате ISO с временем
                finish = serverFinish,     // Строка или null
                link = null                // Пока не используем ссылки
            )
        }
    }

    private fun showDatePicker(editText: android.widget.EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Формируем красивую строку для поля (dd.MM.yyyy)
            val localDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(localDate.format(displayFormatter))
        }, year, month, day)

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
