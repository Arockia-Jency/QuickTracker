package com.example.quicktasker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.quicktasker.data.TaskStore
import com.example.quicktasker.models.Task
import com.example.quicktasker.models.TaskPriority
import com.example.quicktasker.models.TaskStatus

class TaskViewModel : ViewModel() {
    val tasks: LiveData<List<Task>> = TaskStore.tasks

    val totalCount = MediatorLiveData<Int>().apply {
        addSource(tasks) { value = it.size }
    }

    val pendingCount = MediatorLiveData<Int>().apply {
        addSource(tasks) { list -> value = list.count { it.status == TaskStatus.IN_PROGRESS } }
    }

    val urgentCount = MediatorLiveData<Int>().apply {
        addSource(tasks) { list ->
            value = list.count { it.status == TaskStatus.IN_PROGRESS && it.priority == TaskPriority.HIGH }
        }
    }

    val doneCount = MediatorLiveData<Int>().apply {
        addSource(tasks) { list -> value = list.count { it.status == TaskStatus.DONE } }
    }

    val streakCount = MediatorLiveData<Int>().apply {
        // Lightweight placeholder: "streak" == consecutive completed tasks from newest backwards.
        addSource(tasks) { list ->
            value = list.asReversed().takeWhile { it.status == TaskStatus.DONE }.size
        }
    }

    fun seedIfEmpty() = TaskStore.seedIfEmpty()

    fun addTask(title: String, description: String, dueDateMillis: Long?, priority: TaskPriority): Task =
        TaskStore.addTask(title, description, dueDateMillis, priority)

    fun getTask(taskId: Long): Task? = TaskStore.getTask(taskId)

    fun deleteTask(taskId: Long) = TaskStore.deleteTask(taskId)

    fun markComplete(taskId: Long) = TaskStore.markComplete(taskId)
}