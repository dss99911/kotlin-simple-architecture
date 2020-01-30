package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task

class MockTaskApi : TaskApi {

    val tasks = mutableListOf<Task>()
    override suspend fun getTasks(): List<Task> {
        return tasks
    }

    override suspend fun getTask(taskId: String): Task? =
        tasks.findLast { it.id == taskId }


    override suspend fun saveTask(taskId: String, task: Task) {
        //if exists, throw error
        tasks.removeAll { it.id == taskId }
        tasks.add(task)
    }

    override suspend fun completeTask(taskId: String) {
        getTask(taskId)!!.isCompleted = true
    }

    override suspend fun activateTask(taskId: String) {
        getTask(taskId)!!.isCompleted = false
    }

    override suspend fun clearCompletedTasks() {
        tasks.removeAll { it.isCompleted }
    }

    override suspend fun deleteAllTasks() {
        tasks.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
    }
}