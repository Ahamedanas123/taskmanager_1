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
import com.example.taskmanager1.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

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
        initializeViewModel()

        // Initialize RecyclerView
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Initial load
        viewModel.loadTasksFromJson()
    }

    private fun initializeViewModel() {
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.getJsonService(this)
        val repository = TaskRepository(database.taskDao(), this)  // Pass context
        val factory = ViewModelFactory(repository)  // Pass context
        viewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)
    }




    private fun setupRecyclerView() {
        adapter = TaskAdapter(emptyList()) { task ->
            // Task click handler
            val bundle = Bundle().apply { putString("task_title", task.title) }
            firebaseAnalytics.logEvent("Task_Viewed", bundle)

            // Open Edit/Delete dialog or navigate to another screen
            showEditDeleteDialog(task)
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
    }


    private fun setupClickListeners() {
        binding.addTaskButton.setOnClickListener {
            // Open a dialog to enter task details
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
            val taskTitleInput = dialogView.findViewById<EditText>(R.id.taskTitleInput)
            val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.taskDescriptionInput)

            AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val title = taskTitleInput.text.toString()
                    val description = taskDescriptionInput.text.toString()

                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val newTask = Task(
                            id = generateTaskId(),  // Auto-increment ID
                            title = title,
                            description = description,
                            isCompleted = false
                        )
                        viewModel.createTask(newTask)  // Add to the database
                        Snackbar.make(binding.root, "Task added!", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, "Title and Description cannot be empty!", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }


    private fun showEditDeleteDialog(task: Task) {
        // Simple Snackbar for demo purposes
        Snackbar.make(binding.root, "Edit/Delete Task: ${task.title}", Snackbar.LENGTH_LONG)
            .setAction("Delete") {
                viewModel.deleteTask(task)
                Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun generateTaskId(): Int {
        val currentTasks = viewModel.allTasks.value ?: emptyList()
        return if (currentTasks.isNotEmpty()) currentTasks.maxOf { it.id } + 1 else 1
    }




}
