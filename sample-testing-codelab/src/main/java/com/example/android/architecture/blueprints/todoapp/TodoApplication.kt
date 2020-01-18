package com.example.android.architecture.blueprints.todoapp

import kim.jeonghyeon.androidlibrary.BaseApplication
import org.koin.core.module.Module

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class TodoApplication : BaseApplication() {
    override fun getKoinModules(): List<Module> {
        return listOf(appModule)
    }
}