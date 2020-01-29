package com.example.android.architecture.blueprints.todoapp

import com.example.android.architecture.blueprints.todoapp.data.source.remote.MockTaskApi
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TaskApi
import org.koin.dsl.module

val mockModule = module(override = true) {
    single<TaskApi> { MockTaskApi() }
}