package com.google.samples.apps.sunflower

import com.google.samples.apps.sunflower.di.koinModuleApp
import kim.jeonghyeon.androidlibrary.BaseApplication
import org.koin.core.module.Module

class GardenApplication : BaseApplication() {
    override fun getKoinModules(): List<Module> = listOf(koinModuleApp)
}