/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.TaskdetailFragBinding
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : MvvmFragment() {
    val viewModel: TaskDetailViewModel by addingViewModel { TaskDetailViewModel(getNavArgs()) }
    override val layoutId: Int
        get() = R.layout.taskdetail_frag


    init {
        setMenu(R.menu.taskdetail_fragment_menu) {
            when (it.itemId) {
                R.id.menu_delete -> {
                    viewModel.onDeleteClick()
                    true
                }
                else -> false
            }
        }
    }
}
