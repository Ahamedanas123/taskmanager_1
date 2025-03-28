package com.example.taskmanager1.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") @Expose val id: Int = 0,
    @SerializedName("title") @Expose val title: String = "",
    @SerializedName("description") @Expose val description: String = "",
    @SerializedName("isCompleted") @Expose val isCompleted: Boolean = false
)
