/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.room.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface TasksDao : TaskRepository {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Tasks")
    override suspend fun getTasks(): List<Task>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    override suspend fun getTask(taskId: String): Task

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveTask(task: Task)

    @Update
    override suspend fun completeTask(task: Task)

    @Update
    override suspend fun activateTask(task: Task)

    @Query("UPDATE Tasks SET completed = 1 WHERE entryid = :taskId")
    override suspend fun completeTask(taskId: String)

    @Query("UPDATE Tasks SET completed = 0 WHERE entryid = :taskId")
    override suspend fun activateTask(taskId: String)

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Tasks WHERE completed = 1")
    override suspend fun clearCompletedTasks()

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    override suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM Tasks")
    override suspend fun deleteAllTasks()
}