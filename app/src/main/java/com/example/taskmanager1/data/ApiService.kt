package com.example.taskmanager1.data


import android.content.Context
import com.example.taskmanager1.models.Task
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiService {
    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun createTask(@Body task: Task): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: Task): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}

object RetrofitClient {
    fun getJsonService(context: Context): ApiService {
        val jsonString = context.assets.open("db.json").bufferedReader().use { it.readText() }

        val mockRetrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Required but unused
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return mockRetrofit.create(ApiService::class.java)
    }
}
