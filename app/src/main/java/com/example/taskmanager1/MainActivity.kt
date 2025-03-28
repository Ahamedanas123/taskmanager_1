package com.example.taskmanager1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager1.adapter.TaskAdapter
import com.example.taskmanager1.data.AppDatabase
import com.example.taskmanager1.data.RetrofitClient
import com.example.taskmanager1.data.TaskRepository
import com.example.taskmanager1.databinding.ActivityMainBinding
import com.example.taskmanager1.models.Task
import com.example.taskmanager1.viewmodel.TaskViewModel
import com.example.taskmanager1.viewmodel.TaskViewModel.JsonState
import com.example.taskmanager1.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics

        // Setup Database and ViewModel
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.getJsonService(this)
        val repository = TaskRepository(database.taskDao(), apiService)

        viewModel = ViewModelProvider(this, ViewModelFactory(repository)).get(TaskViewModel::class.java)

        // Initialize RecyclerView
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Initial load
        viewModel.loadTasksFromJson()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(emptyList()) { task ->
            // Task click handler
            val bundle = Bundle().apply { putString("task_title", task.title) }
            firebaseAnalytics.logEvent("Task_Viewed", bundle)

            // Delete task
            viewModel.removeTask(task.id)
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupObservers() {
        // Observe Room database tasks
        viewModel.allTasks.observe(this) { tasks ->
            tasks?.let { adapter.updateTasks(it) }
        }

        // Observe JSON operations state
        viewModel.jsonStateLiveData.observe(this) { state ->
            when (state) {
                is JsonState.Loading -> showLoading(true)
                is JsonState.Success -> {
                    showLoading(false)
                    adapter.updateTasks(state.tasks)
                }
                is JsonState.Error -> {
                    showLoading(false)
                    showError(state.message)
                    Timber.e("JSON Error: ${state.message}")
                }
                JsonState.Idle -> {} // Do nothing
            }
        }
    }

    private fun setupClickListeners() {
        binding.addTaskButton.setOnClickListener {
            val newTask = Task(
                title = "New Task",
                description = "New Task Description",
                isCompleted = true
            )
            viewModel.createTask(newTask)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}