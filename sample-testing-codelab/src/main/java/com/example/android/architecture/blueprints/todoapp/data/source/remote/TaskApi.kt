package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import retrofit2.http.*

interface TaskApi {
    @GET("/tasks")
    suspend fun getTasks(): List<Task>

    @GET("/tasks/{taskId}")
    suspend fun getTask(@Path("taskId") taskId: String): Task?

    @PUT("/tasks/{taskId}")
    suspend fun saveTask(@Path("taskId") taskId: String, @Body task: Task)

    @PATCH("/tasks/{taskId}/complete")
    suspend fun completeTask(@Path("taskId") taskId: String)

    @PATCH("/tasks/{taskId}/activate")
    suspend fun activateTask(@Path("taskId") taskId: String)

    @DELETE("/tasks/complete")
    suspend fun clearCompletedTasks()

    @DELETE("/tasks")
    suspend fun deleteAllTasks()

    @DELETE("/tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: String)
}