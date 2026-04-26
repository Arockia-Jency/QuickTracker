package com.example.quicktasker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quicktasker.databinding.FragmentTasksBinding
import com.example.quicktasker.models.Task
import com.example.quicktasker.viewmodel.TaskViewModel

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private var lastFullList: List<Task> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTasksBinding.bind(view)

        taskViewModel.seedIfEmpty()
        setupRecyclerView()
        setupSearch()
        observeTasks()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(emptyList()) { task ->
            (activity as? MainActivity)?.openTaskDetails(task.id)
        }
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tasksRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            submitFilteredList(query)
        }
    }

    private fun observeTasks() {
        taskViewModel.tasks.observe(viewLifecycleOwner) { list ->
            lastFullList = list
            submitFilteredList(binding.searchEditText.text?.toString().orEmpty())
        }

        taskViewModel.totalCount.observe(viewLifecycleOwner) { binding.totalCountText.text = it.toString() }
        taskViewModel.pendingCount.observe(viewLifecycleOwner) { binding.pendingCountText.text = it.toString() }
        taskViewModel.urgentCount.observe(viewLifecycleOwner) { binding.urgentCountText.text = it.toString() }
        taskViewModel.doneCount.observe(viewLifecycleOwner) { binding.doneCountText.text = it.toString() }
    }

    private fun submitFilteredList(queryRaw: String) {
        val query = queryRaw.trim()
        if (query.isEmpty()) {
            adapter.submitList(lastFullList)
            return
        }

        val q = query.lowercase()
        val filtered = lastFullList.filter { task ->
            task.title.lowercase().contains(q) || task.description.lowercase().contains(q)
        }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}