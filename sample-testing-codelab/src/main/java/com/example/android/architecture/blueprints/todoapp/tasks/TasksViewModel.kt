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
package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.example.android.architecture.blueprints.todoapp.util.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.EDIT_RESULT_OK
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.launch
import kotlinx.coroutines.launch
import java.util.*

/**
 * Exposes the data to be used in the task list screen.
 *
 *
 * [BaseObservable] implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a [Bindable] annotation to the property's
 * getter method.
 */
class TasksViewModel(
    val args: TasksFragmentArgs,
    private val tasksRepository: TaskRepository
) : BaseViewModel() {

    val items: LiveResource<List<TaskItemViewModel>> = liveResource()
    val currentFilteringLabel: LiveObject<Int> = liveObject()
    val noTasksLabel: LiveObject<Int> = liveObject()
    val noTaskIconRes: LiveObject<Int> = liveObject()
    val tasksAddViewVisible: LiveObject<Boolean> = liveObject()
    val snackbarMessage: LiveObject<Int> = liveObject<Int>().apply {
        addSource(items) {
            if (it.isError()) {
                call(R.string.loading_tasks_error)
            }
        }

    }
    private var currentFiltering = TasksFilterType.ALL_TASKS
    val openTaskEvent: LiveObject<String> = liveObject()
    val newTaskEvent: LiveObject<Unit> = liveObject()

    override fun onCreate() {
        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS)

        loadTasks(true)

        showEditResultMessage(args.userMessage)
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be [TasksFilterType.ALL_TASKS],
     * [TasksFilterType.COMPLETED_TASKS], or
     * [TasksFilterType.ACTIVE_TASKS]
     */
    private fun setFiltering(requestType: TasksFilterType) {
        currentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill, true)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp, false)
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp, false)
            }
        }
    }

    private fun setFilter(@StringRes filteringLabelString: Int, @StringRes noTasksLabelString: Int,
            @DrawableRes noTaskIconDrawable: Int, tasksAddVisible: Boolean) {
        currentFilteringLabel.value = filteringLabelString
        noTasksLabel.value = noTasksLabelString
        noTaskIconRes.value = noTaskIconDrawable
        tasksAddViewVisible.value = tasksAddVisible

    }

    fun onClearMenuClicked() {
        launch {
            tasksRepository.clearCompletedTasks()
            snackbarMessage.value = R.string.completed_tasks_cleared
            loadTasks(false)
        }
    }

    internal fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    /**
     * Called by the [TasksAdapter].
     */
    internal fun openTask(taskId: String) {
        openTaskEvent.call(taskId)
    }

    private fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> snackbarMessage.call(R.string.successfully_saved_task_message)
            ADD_EDIT_RESULT_OK -> snackbarMessage.call(R.string.successfully_added_task_message)
            DELETE_RESULT_OK -> snackbarMessage.call(R.string.successfully_deleted_task_message)
        }

    }

    private fun showSnackbarMessage(message: Int) {
        snackbarMessage.value = message
    }

    fun onRefreshMenuClicked() {
        loadTasks(true)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [TasksDataSource]
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean) {
        //todo how to force update
        items.load {
            tasksRepository.getTasks()
                .filter()
                .map { TaskItemViewModel(it, this@TasksViewModel) }
        }
    }

    private fun List<Task>.filter(): List<Task> {
        val tasksToShow = ArrayList<Task>()
        // We filter the tasks based on the requestType
        for (task in this) {
            when (currentFiltering) {
                TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                    tasksToShow.add(task)
                }
                TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                    tasksToShow.add(task)
                }
            }
        }
        return tasksToShow
    }

    fun onMenuItemClicked(@MenuRes itemId: Int) {
        setFiltering(
            when (itemId) {
                R.id.active -> TasksFilterType.ACTIVE_TASKS
                R.id.completed -> TasksFilterType.COMPLETED_TASKS
                else -> TasksFilterType.ALL_TASKS
            }
        )
        loadTasks(false)

    }
}
