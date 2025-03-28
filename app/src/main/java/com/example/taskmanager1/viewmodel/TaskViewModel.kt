package com.example.taskmanager1.viewmodel

import androidx.lifecycle.*
import com.example.taskmanager1.data.TaskRepository
import com.example.taskmanager1.models.Task
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<Task>> = repository.allTasks

    init {
        loadTasksFromJson()
    }

    fun loadTasksFromJson() = viewModelScope.launch {
        try {
            val tasks = repository.loadTasksFromJson()
            tasks.forEach { task ->
                repository.insert(task)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createTask(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}
