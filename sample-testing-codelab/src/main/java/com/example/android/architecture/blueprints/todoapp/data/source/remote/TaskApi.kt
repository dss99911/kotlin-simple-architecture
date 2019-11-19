package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import kim.jeonghyeon.androidlibrary.architecture.net.api

interface TaskApi {

    suspend fun getTasks(): List<Task>

    suspend fun getTask(taskId: String): Task

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)

    companion object {
        fun create(): TaskApi {
            return api("http://example.com")
        }
    }
}