package com.example.android.architecture.blueprints.todoapp

import com.balancehero.example.androidtesting.BaseUnitTest
import org.koin.core.module.Module

abstract class TodoUnitTest : BaseUnitTest() {
    override val modules: List<Module>
        get() = appModule
}