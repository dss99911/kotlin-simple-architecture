package com.example.android.architecture.blueprints.todoapp.util

abstract class BaseApiTest<T> : BaseKoinTest() {
    abstract val api: T
}