package com.example.quicktasker.models

enum class TaskPriority { LOW, MEDIUM, HIGH }

enum class TaskStatus { IN_PROGRESS, DONE }

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dueDateMillis: Long?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val progress: Int
)