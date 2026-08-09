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
import androidx.navigation.fragment.findNavController
import ru.netology.network.databinding.FragmentAddWorkBinding

class AddWorkFragment : Fragment() {

    private var _binding: FragmentAddWorkBinding? = null
    private val binding get() = _binding!!

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

        // Обработка системных отступов (статус-бар, навигационная панель)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Кнопка «Назад» — просто возвращаемся на предыдущий экран
        binding.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка-галочка «Сохранить» — пока только демонстрация навигации
        binding.ivCheckMark.setOnClickListener {
            val company = binding.etCompany.text.toString().trim()
            val position = binding.etPosition.text.toString().trim()
            val period = binding.etPeriod.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            // Простая валидация: хотя бы компания и должность должны быть
            if (company.isEmpty() || position.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Заполните название компании и должность",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Здесь завтра подключим ViewModel и сохранение
            Toast.makeText(
                requireContext(),
                "Работа сохранена:\n$company — $position",
                Toast.LENGTH_LONG
            ).show()

            // Возвращаемся назад после «сохранения»
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
