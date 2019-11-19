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

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TaskApi

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class DefaultTasksRepository(
    private val tasksDao: TasksDao = ToDoDatabase.instance.taskDao(),
    private val taskApi: TaskApi = TaskApi.create()
) {

    suspend fun getTasks(forceUpdate: Boolean): List<Task> {
        if (!forceUpdate) {
            val tasks = tasksDao.getTasks()
            if (tasks.isNotEmpty()) {
                return tasks
            }
        }

        return taskApi.getTasks().also {
            refreshLocalDataSource(it)
        }
    }

    suspend fun getTask(taskId: String, forceUpdate: Boolean): Task {
        if (!forceUpdate) {
            tasksDao.getTaskById(taskId)?.let {
                return it
            }
        }

        return taskApi.getTask(taskId).also {
            refreshLocalDataSource(it)
        }
    }

    suspend fun deleteTask(taskId: String) {
        taskApi.deleteTask(taskId)
        tasksDao.deleteTaskById(taskId)
    }

    suspend fun activateTask(task: Task)  {
        taskApi.activateTask(task)
        tasksDao.updateCompleted(task.id, false)
    }

    suspend fun completeTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        taskApi.completeTask(task)
        tasksDao.updateCompleted(task.id, true)
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        tasksDao.deleteTasks()
        for (task in tasks) {
            tasksDao.insertTask(task)
        }
    }
    private suspend fun refreshLocalDataSource(task: Task) {
        tasksDao.insertTask(task)
    }
}
