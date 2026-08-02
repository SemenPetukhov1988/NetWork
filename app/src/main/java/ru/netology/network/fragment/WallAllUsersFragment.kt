package ru.netology.network.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.network.R
import ru.netology.network.databinding.FragmentWallAllUsersBinding

class WallAllUsersFragment : Fragment() {

    private var _binding: FragmentWallAllUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWallAllUsersBinding.inflate(inflater, container, false)

        // Тут твоя логика инициализации RecyclerView
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.postsRecyclerView.adapter = ...

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
