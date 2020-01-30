package com.example.android.architecture.blueprints.todoapp

import com.example.android.architecture.blueprints.todoapp.util.BaseKoinTest
import org.koin.core.module.Module

abstract class TodoKoinTest : BaseKoinTest() {
    override val modules: List<Module>
        get() = appModule
}