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

package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.architecture.livedata.getData
import kim.jeonghyeon.androidlibrary.architecture.livedata.liveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

/**
 * ViewModel for the Add/Edit screen.
 *
 *
 * This ViewModel only exposes [ObservableField]s, so it doesn't need to extend
 * [androidx.databinding.BaseObservable] and updates are notified automatically. See
 * [com.example.android.architecture.blueprints.todoapp.statistics.StatisticsViewModel] for
 * how to deal with more complex scenarios.
 */
class AddEditTaskViewModel(
    private val navArgs: AddEditTaskFragmentArgs,
    private val tasksRepository: TaskRepository
) : BaseViewModel() {

    val task = liveResource<Task>().apply {
        load {
            val taskid = navArgs.taskid
            if (taskid == null) {
                Task()
            } else {
                tasksRepository.getTask(taskid) ?: Task()
            }
        }
    }

    // Called when clicking on fab.
    fun onClickFAB() {
        state.load {
            tasksRepository.saveTask(task.getData()!!)
            navigateToTasksFragment()
        }
    }

    private fun navigateToTasksFragment() {
        navigateUp()
    }
}
