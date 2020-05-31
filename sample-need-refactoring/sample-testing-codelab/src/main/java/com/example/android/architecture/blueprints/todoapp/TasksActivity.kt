/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp

import android.os.Bundle
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseActivity
import kotlinx.android.synthetic.main.tasks_act.*

class TasksActivity : BaseActivity() {
    override val layoutId = R.layout.tasks_act

    override val navHostId = R.id.nav_host_fragment

    override val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(R.id.tasksFragment, R.id.statisticsFragment)
            .setDrawerLayout(drawer_layout)
            .build()
    }

    ////TODO HYUN [baselivedata] : task 추가한 후, 백하면 TasksActivity 가 다시 보이는 현상
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)
        navView.setupWithNavController(navController)
    }
}
