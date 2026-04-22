package com.example.quicktasker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quicktasker.databinding.FragmentTasksBinding
import com.example.quicktasker.viewmodel.TaskViewModel

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTasksBinding.bind(view)

        taskViewModel.seedIfEmpty()
        setupRecyclerView()
        observeTasks()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(emptyList()) { task ->
            (activity as? MainActivity)?.openTaskDetails(task.id)
        }
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tasksRecyclerView.adapter = adapter
    }

    private fun observeTasks() {
        taskViewModel.tasks.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        taskViewModel.totalCount.observe(viewLifecycleOwner) { binding.totalCountText.text = it.toString() }
        taskViewModel.pendingCount.observe(viewLifecycleOwner) { binding.pendingCountText.text = it.toString() }
        taskViewModel.urgentCount.observe(viewLifecycleOwner) { binding.urgentCountText.text = it.toString() }
        taskViewModel.doneCount.observe(viewLifecycleOwner) { binding.doneCountText.text = it.toString() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}