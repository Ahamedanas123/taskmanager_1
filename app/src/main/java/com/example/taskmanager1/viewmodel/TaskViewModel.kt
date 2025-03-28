package com.example.taskmanager1.viewmodel

import androidx.lifecycle.*
import com.example.taskmanager1.data.TaskRepository
import com.example.taskmanager1.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber // or use Log

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // For Room database tasks
    val allTasks: LiveData<List<Task>> = repository.allTasks

    // For JSON operations state
    private val _jsonState = MutableStateFlow<JsonState>(JsonState.Idle)
    val jsonStateLiveData: LiveData<JsonState> = _jsonState.asLiveData()

    sealed class JsonState {
        object Idle : JsonState()
        object Loading : JsonState()
        data class Success(val tasks: List<Task>) : JsonState()
        data class Error(val message: String) : JsonState()
    }

    // Room operations with error handling
    fun insert(task: Task) = viewModelScope.launch {
        try {
            repository.insert(task)
        } catch (e: Exception) {
            Timber.e(e, "Failed to insert task")
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        try {
            repository.update(task)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update task")
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        try {
            repository.delete(task)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete task")
        }
    }

    // JSON operations with state management
    fun loadTasksFromJson() = viewModelScope.launch {
        _jsonState.value = JsonState.Loading
        try {
            val tasks = repository.fetchTasksFromJson()
            _jsonState.value = JsonState.Success(tasks)
        } catch (e: Exception) {
            _jsonState.value = JsonState.Error(e.message ?: "Unknown error")
            Timber.e(e, "Failed to load JSON tasks")
        }
    }

    fun createTask(task: Task) = viewModelScope.launch {
        try {
            repository.addTask(task)
            loadTasksFromJson() // Refresh after creation
        } catch (e: Exception) {
            Timber.e(e, "Failed to create task")
        }
    }

    fun modifyTask(id: Int, task: Task) = viewModelScope.launch {
        try {
            repository.modifyTask(id, task)
            loadTasksFromJson() // Refresh after modification
        } catch (e: Exception) {
            Timber.e(e, "Failed to modify task")
        }
    }

    fun removeTask(id: Int) = viewModelScope.launch {
        try {
            repository.removeTask(id)
            loadTasksFromJson() // Refresh after deletion
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove task")
        }
    }
}