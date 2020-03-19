package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.data.Task
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

data class TaskItemViewModel(val task: Task, val parent: TasksViewModel) :
    DiffComparable<TaskItemViewModel> {

    //if user change title, even if id is same. content is different. but recyclerview consider that if id is same. then no need to draw again.
    override fun areItemsTheSame(item: TaskItemViewModel) = areContentsTheSame(item)

    override fun areContentsTheSame(item: TaskItemViewModel): Boolean = task == item.task

    fun onCompleteChanged() {
        parent.completeTask(task, task.isCompleted)
    }

    fun onTaskClicked() {
        parent.openTask(task.id)
    }
}