package com.example.taskmanager1

import android.app.Application
import com.example.taskmanager1.data.AppDatabase
import com.example.taskmanager1.data.TaskRepository
import com.example.taskmanager1.viewmodel.TaskViewModel

class TaskManagerApplication : Application() {

    lateinit var taskRepository: TaskRepository
    lateinit var viewModel: TaskViewModel

    override fun onCreate() {
        super.onCreate()

        // Initialize the database and repository
        val database = AppDatabase.getDatabase(this)
        taskRepository = TaskRepository(database.taskDao(), this)

        // Initialize the ViewModel
        viewModel = TaskViewModel(taskRepository)
    }
}
