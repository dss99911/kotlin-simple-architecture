package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import kim.jeonghyeon.androidlibrary.architecture.net.error.MessageCodeError

class MockTaskApi : TaskApi {
    val tasks = mutableListOf<Task>()
    override suspend fun getTasks(): List<Task> {
        return tasks
    }

    override suspend fun getTask(taskId: String): Task =
        tasks.findLast { it.id == taskId }
            ?: throw MessageCodeError(
                TodoResponseCodeConstants.ERROR_TASK_NOT_EXISTS,
                "task not exists"
            )

    override suspend fun saveTask(task: Task) {
        //if exists, throw error
        if (tasks.any { it.id == task.id }) {
            throw MessageCodeError(
                TodoResponseCodeConstants.ERROR_TASK_DUPLICATED,
                "duplicated task id"
            )
        }
        tasks.add(task)
    }

    override suspend fun completeTask(taskId: String) {
        getTask(taskId).isCompleted = true
    }

    override suspend fun activateTask(taskId: String) {
        getTask(taskId).isCompleted = false
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