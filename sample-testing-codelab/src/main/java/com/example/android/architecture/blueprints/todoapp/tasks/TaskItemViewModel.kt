package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.data.Task
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

data class TaskItemViewModel(val task: Task, val parent: TasksViewModel) :
    DiffComparable<TaskItemViewModel> {

    override fun areItemsTheSame(item: TaskItemViewModel) = task.id == item.task.id

    override fun areContentsTheSame(item: TaskItemViewModel) = areItemsTheSame(item)

    fun onCompleteChanged() {
        parent.completeTask(task, task.isCompleted)
    }

    fun onTaskClicked() {
        parent.openTask(task.id)
    }
}