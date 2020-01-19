package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.architecture.net.api
import retrofit2.http.*

interface TaskApi : TaskRepository {
    @GET("/tasks")
    override suspend fun getTasks(): List<Task>

    @GET("/tasks/{taskId}")
    override suspend fun getTask(@Path("taskId") taskId: String): Task

    @PUT("/tasks")
    override suspend fun saveTask(task: Task)

    @POST("/tasks/{taskId}/complete")
    override suspend fun completeTask(@Path("taskId") taskId: String)

    @POST("/tasks/{taskId}/activate")
    override suspend fun activateTask(@Path("taskId") taskId: String)

    @DELETE("/tasks/clear-completed")
    override suspend fun clearCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @DELETE("/tasks/clear-all")
    override suspend fun deleteAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @DELETE("/tasks/{taskId}")
    override suspend fun deleteTask(@Path("taskId") taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun create(): TaskApi {
            return api("http://example.com")
        }
    }
}