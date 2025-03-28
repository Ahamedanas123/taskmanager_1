package com.example.taskmanager1.data

import android.content.Context
import com.example.taskmanager1.models.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class TaskRepository(private val taskDao: TaskDao, private val context: Context) {

    val allTasks = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
        updateJsonFile()
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        updateJsonFile()
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
        updateJsonFile()
    }

    suspend fun loadTasksFromJson(): List<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "db.json")
                if (!file.exists()) {
                    // Copy from assets to internal storage if not exists
                    context.assets.open("db.json").use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                val json = file.readText()
                val taskListType = object : TypeToken<List<Task>>() {}.type
                Gson().fromJson(json, taskListType) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Function to update JSON file with a safer Gson configuration
    private suspend fun updateJsonFile() {
        withContext(Dispatchers.IO) {
            try {
                // Use a regular Gson configuration without exposing specific annotations
                val gson = GsonBuilder().setPrettyPrinting().create()
                val tasks = taskDao.getAllTasks().value ?: emptyList()
                val json = gson.toJson(tasks)
                val file = File(context.filesDir, "db.json")
                if (!file.exists()) {
                    context.assets.open("db.json").use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                file.writeText(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
