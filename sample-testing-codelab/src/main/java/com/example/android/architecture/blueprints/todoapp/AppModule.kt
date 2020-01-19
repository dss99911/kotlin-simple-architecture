package com.example.android.architecture.blueprints.todoapp

import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskFragmentArgs
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskViewModel
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepositoryImpl
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TaskApi
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsViewModel
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailFragmentArgs
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailViewModel
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragmentArgs
import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (args: AddEditTaskFragmentArgs) -> AddEditTaskViewModel(args, get()) }
    viewModel { StatisticsViewModel(get()) }
    viewModel { (args: TaskDetailFragmentArgs) -> TaskDetailViewModel(args, get()) }
    viewModel { (args: TasksFragmentArgs) -> TasksViewModel(args, get()) }
}

val dataModule = module {
    single { ToDoDatabase.instance.taskDao() }
    single { TaskApi.create() }
    single<TaskRepository> { TasksRepositoryImpl(get(), get()) }
}

val appModule = listOf(viewModelModule, dataModule)