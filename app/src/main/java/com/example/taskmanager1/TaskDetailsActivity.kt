package com.example.taskmanager1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager1.data.AppDatabase
import com.example.taskmanager1.data.RetrofitClient
import com.example.taskmanager1.data.TaskRepository
import com.example.taskmanager1.databinding.ActivityTaskDetailsBinding
import com.example.taskmanager1.models.Task
import com.example.taskmanager1.viewmodel.TaskViewModel
import com.example.taskmanager1.viewmodel.ViewModelFactory

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        initializeViewModel()

        val taskId = intent.getIntExtra("task_id", -1)
        val taskTitle = intent.getStringExtra("task_title") ?: ""
        val taskDescription = intent.getStringExtra("task_description") ?: ""

        // Set initial data
        binding.editTaskTitle.setText(taskTitle)
        binding.editTaskDescription.setText(taskDescription)

        binding.saveButton.setOnClickListener {
            val updatedTask = Task(
                id = taskId,
                title = binding.editTaskTitle.text.toString(),
                description = binding.editTaskDescription.text.toString(),
                isCompleted = false
            )
            // Update the task using the ViewModel
            viewModel.updateTask(updatedTask)
            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.deleteButton.setOnClickListener {
            val task = Task(
                id = taskId,
                title = binding.editTaskTitle.text.toString(),
                description = binding.editTaskDescription.text.toString(),
                isCompleted = false
            )
            viewModel.deleteTask(task)
            Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao(), this)  // Pass context here
        val factory = ViewModelFactory(repository)  // Pass context here as well
        viewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)
    }

}
