package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.network.R
import ru.netology.network.databinding.FragmentEventsAllUsersBinding

class EventsAllUsersFragment : Fragment() {

    private var _binding: FragmentEventsAllUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsAllUsersBinding.inflate(inflater, container, false)

        // Базовая настройка RecyclerView
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.eventsRecyclerView.adapter = ... (сюда потом подставишь адаптер)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
