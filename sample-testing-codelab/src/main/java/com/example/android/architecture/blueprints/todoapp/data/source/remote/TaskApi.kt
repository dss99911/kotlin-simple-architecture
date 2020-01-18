package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.architecture.net.api

interface TaskApi : TaskRepository {
    //todo set api
    companion object {
        fun create(): TaskApi {
            return api("http://example.com")
        }
    }
}