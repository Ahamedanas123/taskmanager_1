package com.example.taskmanager1.data

import androidx.lifecycle.LiveData
import com.example.taskmanager1.models.Task

class TaskRepository(private val taskDao: TaskDao, private val apiService: ApiService) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) = taskDao.insertTask(task)
    suspend fun update(task: Task) = taskDao.updateTask(task)
    suspend fun delete(task: Task) = taskDao.deleteTask(task)

    suspend fun fetchTasksFromJson(): List<Task> = apiService.getTasks()
    suspend fun addTask(task: Task): Task = apiService.createTask(task)
    suspend fun modifyTask(id: Int, task: Task): Task = apiService.updateTask(id, task)
    suspend fun removeTask(id: Int) = apiService.deleteTask(id)
}
