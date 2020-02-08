/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksDao
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TaskApi
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.repository.NetworkBoundResource

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class TasksRepositoryImpl(
    private val tasksDao: TasksDao,
    private val taskApi: TaskApi
) : TaskRepository {

    override suspend fun getTasks(): List<Task> {
        val tasks = tasksDao.getTasks()
        if (tasks.isNotEmpty()) {
            return tasks
        }

        return taskApi.getTasks().also {
            refreshLocalDataSource(it)
        }
    }

    override fun getLiveTasks(): LiveResource<List<Task>> =
        object : NetworkBoundResource<List<Task>, List<Task>>() {
            override fun loadFromDb(): LiveData<List<Task>> = tasksDao.getLiveTasks()
            override fun shouldFetch(data: List<Task>?): Boolean = data.isNullOrEmpty()
            override suspend fun createCall(): List<Task> = taskApi.getTasks()
            override suspend fun saveCallResult(item: List<Task>) = refreshLocalDataSource(item)
        }

    override suspend fun getTask(taskId: String): Task? {
        @Suppress("UNNECESSARY_SAFE_CALL")
        tasksDao.getTask(taskId)?.let {
            return it
        }

        return taskApi.getTask(taskId)?.also {
            refreshLocalDataSource(it)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        taskApi.deleteTask(taskId)
        tasksDao.deleteTask(taskId)
    }

    override suspend fun saveTask(task: Task) {
        taskApi.saveTask(task.id, task)
        tasksDao.saveTask(task)
    }

    override suspend fun completeTask(task: Task) {
        task.isCompleted = true
        taskApi.completeTask(task.id)
        tasksDao.completeTask(task.id)
    }

    override suspend fun completeTask(taskId: String) {
        completeTask(getTask(taskId)!!)
    }

    override suspend fun activateTask(task: Task) {
        task.isCompleted = false
        taskApi.activateTask(task.id)
        tasksDao.activateTask(task.id)
    }

    override suspend fun activateTask(taskId: String) {
        activateTask(getTask(taskId)!!)
    }

    override suspend fun clearCompletedTasks() {
        taskApi.clearCompletedTasks()
        tasksDao.clearCompletedTasks()
    }

    override suspend fun deleteAllTasks() {
        taskApi.deleteAllTasks()
        tasksDao.deleteAllTasks()
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        tasksDao.deleteAllTasks()
        for (task in tasks) {
            tasksDao.saveTask(task)
        }
    }
    private suspend fun refreshLocalDataSource(task: Task) {
        tasksDao.saveTask(task)
    }
}
