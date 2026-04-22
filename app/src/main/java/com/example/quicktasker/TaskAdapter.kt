package com.example.quicktasker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quicktasker.databinding.ItemTaskBinding // Generated from item_task.xml
import com.example.quicktasker.models.Task
import com.example.quicktasker.models.TaskPriority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private var taskList: List<Task>,
    private val onTaskClick: (Task) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Use ViewBinding for the items to keep it consistent with MainActivity
    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.apply {
            taskTitle.text = task.title
            taskDescription.text = task.description
            taskProgressBar.progress = task.progress

            // Display the new fields
            taskDate.text = task.dueDateMillis?.let { formatDueDate(it) } ?: "No due date"
            priorityBadge.text = task.priority.name

            // Dynamic color for priority (Optional but recommended for the "Exact Design")
            when (task.priority) {
                TaskPriority.HIGH -> priorityBadge.setTextColor(root.context.getColor(R.color.secondary_indigo))
                TaskPriority.MEDIUM -> priorityBadge.setTextColor(root.context.getColor(R.color.primary_deep_teal))
                TaskPriority.LOW -> priorityBadge.setTextColor(root.context.getColor(R.color.text_outline))
            }

            root.setOnClickListener { onTaskClick(task) }
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(newList: List<Task>) {
        taskList = newList
        notifyDataSetChanged()
    }

    private fun formatDueDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}