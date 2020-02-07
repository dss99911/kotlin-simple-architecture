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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.example.android.architecture.blueprints.todoapp.util.DELETE_RESULT_OK
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.getData
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.ctx

/**
 * Listens to user actions from the list item in ([TasksFragment]) and redirects them to the
 * Fragment's actions listener.
 */
class TaskDetailViewModel(
    private val navArgs: TaskDetailFragmentArgs,
    private val tasksRepository: TaskRepository
) : BaseViewModel() {

    val task = LiveResource<Task>()

    override fun onResume() {
        onRefresh()
    }

    fun onCompleteChanged(isChecked: Boolean) {
        setCompleted(isChecked)
    }

    fun onClickDelete() {
        state.load {
            tasksRepository.deleteTask(navArgs.taskid)

            val direction = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToTasksFragment().setUserMessage(DELETE_RESULT_OK)
            navigateDirection(direction)
        }
    }

    fun onClickEdit() {
        val direction = TaskDetailFragmentDirections
            .actionTaskDetailFragmentToAddEditTaskFragment(
                navArgs.taskid,
                ctx.getString(R.string.edit_task)
            )
        navigateDirection(direction)
    }

    private fun setCompleted(completed: Boolean) {
        val task: Task = task.getData() ?: return

        state.load {
            if (completed) {
                tasksRepository.completeTask(task)
                showSnackbar(R.string.task_marked_complete)
            } else {
                tasksRepository.activateTask(task)
                showSnackbar(R.string.task_marked_active)
            }
        }
    }

    fun onRefresh() {
        task.load(state) {
            tasksRepository.getTask(navArgs.taskid)!!
        }
    }
}
