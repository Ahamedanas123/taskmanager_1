package com.example.taskmanager1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager1.R
import com.example.taskmanager1.models.Task

class TaskAdapter(
    private var taskList: List<Task>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.taskTitle)

        fun bind(task: Task) {
            titleTextView.text = task.title
            itemView.setOnClickListener { onTaskClick(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount() = taskList.size

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }
}
