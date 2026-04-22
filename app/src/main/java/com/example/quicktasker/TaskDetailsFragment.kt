package com.example.quicktasker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quicktasker.databinding.FragmentTaskDetailsBinding
import com.example.quicktasker.models.TaskPriority
import com.example.quicktasker.models.TaskStatus
import com.example.quicktasker.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()

    private val taskId: Long by lazy {
        requireArguments().getLong(ARG_TASK_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskDetailsBinding.bind(view)

        binding.backButton.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.deleteButton.setOnClickListener {
            taskViewModel.deleteTask(taskId)
            Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
        binding.markCompleteButton.setOnClickListener {
            taskViewModel.markComplete(taskId)
            Toast.makeText(requireContext(), "Marked complete", Toast.LENGTH_SHORT).show()
        }
        binding.shareButton.setOnClickListener { shareTask() }

        taskViewModel.tasks.observe(viewLifecycleOwner) { render() }
        render()
    }

    private fun render() {
        val task = taskViewModel.getTask(taskId)
        if (task == null) {
            binding.titleText.text = "Task not found"
            binding.descriptionText.text = ""
            binding.dueDateText.text = "—"
            binding.priorityChip.text = ""
            binding.statusChip.text = ""
            binding.currentStatusText.text = ""
            binding.markCompleteButton.isEnabled = false
            binding.deleteButton.isEnabled = false
            return
        }

        binding.titleText.text = task.title
        binding.descriptionText.text = task.description.ifBlank { "—" }
        binding.dueDateText.text = task.dueDateMillis?.let { formatDueDate(it) } ?: "—"

        binding.priorityChip.text = when (task.priority) {
            TaskPriority.HIGH -> "High Priority"
            TaskPriority.MEDIUM -> "Medium Priority"
            TaskPriority.LOW -> "Low Priority"
        }
        binding.statusChip.text = if (task.status == TaskStatus.DONE) "Done" else "In Progress"
        binding.currentStatusText.text = binding.statusChip.text

        binding.markCompleteButton.isEnabled = task.status != TaskStatus.DONE
        binding.markCompleteButton.text =
            if (task.status == TaskStatus.DONE) "Completed" else "Mark as Complete"
    }

    private fun shareTask() {
        val task = taskViewModel.getTask(taskId) ?: return
        val text = buildString {
            appendLine(task.title)
            if (task.description.isNotBlank()) appendLine(task.description)
            val due = task.dueDateMillis?.let { formatDueDate(it) }
            if (due != null) appendLine("Due: $due")
            appendLine("Priority: ${task.priority.name}")
        }.trim()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share task"))
    }

    private fun formatDueDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TASK_ID = "task_id"

        fun newInstance(taskId: Long): TaskDetailsFragment {
            return TaskDetailsFragment().apply {
                arguments = Bundle().apply { putLong(ARG_TASK_ID, taskId) }
            }
        }
    }
}

