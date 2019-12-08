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
package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.AddtaskFragBinding
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.extension.simpleViewModels

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : MvvmFragment<AddEditTaskViewModel, AddtaskFragBinding>() {

    override val layoutId = R.layout.addtask_frag

    override val viewModel: AddEditTaskViewModel by simpleViewModels { AddEditTaskViewModel(getNavArgs()) }

    override fun setVariable(binding: AddtaskFragBinding) {
        binding.viewmodel = viewModel
    }
}
