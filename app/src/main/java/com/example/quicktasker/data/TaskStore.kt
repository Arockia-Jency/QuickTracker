package com.example.quicktasker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quicktasker.models.Task
import com.example.quicktasker.models.TaskPriority
import com.example.quicktasker.models.TaskStatus

object TaskStore {
    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    private var nextId: Long = 1L

    fun seedIfEmpty() {
        if (!_tasks.value.isNullOrEmpty()) return
        _tasks.value = listOf(
            Task(
                id = nextId++,
                title = "Finalize Q4 Marketing Report",
                description = "Review metrics and summarize outcomes for stakeholders.",
                dueDateMillis = null,
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                progress = 65
            ),
            Task(
                id = nextId++,
                title = "Update Design System",
                description = "Add tonal layers and update component tokens.",
                dueDateMillis = null,
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.IN_PROGRESS,
                progress = 30
            ),
            Task(
                id = nextId++,
                title = "Weekly Team Sync",
                description = "Prepare agenda and action items.",
                dueDateMillis = null,
                priority = TaskPriority.LOW,
                status = TaskStatus.DONE,
                progress = 100
            )
        )
    }

    fun addTask(
        title: String,
        description: String,
        dueDateMillis: Long?,
        priority: TaskPriority
    ): Task {
        val task = Task(
            id = nextId++,
            title = title.trim(),
            description = description.trim(),
            dueDateMillis = dueDateMillis,
            priority = priority,
            status = TaskStatus.IN_PROGRESS,
            progress = 0
        )
        _tasks.value = (_tasks.value.orEmpty() + task)
        return task
    }

    fun getTask(taskId: Long): Task? = _tasks.value.orEmpty().firstOrNull { it.id == taskId }

    fun deleteTask(taskId: Long) {
        _tasks.value = _tasks.value.orEmpty().filterNot { it.id == taskId }
    }

    fun markComplete(taskId: Long) {
        _tasks.value = _tasks.value.orEmpty().map { t ->
            if (t.id != taskId) t
            else t.copy(status = TaskStatus.DONE, progress = 100)
        }
    }
}

