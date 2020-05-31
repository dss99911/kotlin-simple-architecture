package com.example.android.architecture.blueprints.todoapp

import kim.jeonghyeon.testing.BaseUnitTest
import org.koin.core.module.Module

abstract class TodoUnitTest : BaseUnitTest() {
    override val modules: List<Module>
        get() = koinModuleApp
}