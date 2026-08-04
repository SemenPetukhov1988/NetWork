package ru.netology.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.network.adapter.EventAdapter
import ru.netology.network.databinding.FragmentEventsAllUsersBinding
import ru.netology.network.viewmodel.GlobalEventViewModel

@AndroidEntryPoint
class EventsAllUsersFragment : Fragment() {

    private var _binding: FragmentEventsAllUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlobalEventViewModel by viewModels()

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsAllUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        adapter = EventAdapter(
            onAuthorClick = { /* TODO: переход в профиль автора события */ },
            onOptionsClick = { /* TODO: меню действий с событием */ }
        )

        binding.eventsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
