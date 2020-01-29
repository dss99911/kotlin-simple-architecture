/*
 * Copyright 2016, The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.tasks

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.android.architecture.blueprints.todoapp.BR
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kotlinx.android.synthetic.main.tasks_frag.*

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : BaseFragment() {

    override val layoutId = R.layout.tasks_frag

    val viewModel: TasksViewModel by bindingViewModel(BR.viewmodel)

    init {
        setMenu(R.menu.tasks_fragment_menu) {
            when (it.itemId) {
                R.id.menu_clear -> {
                    viewModel.onClearMenuClicked()
                    true
                }
                R.id.menu_filter -> {
                    onFilterMenuClicked()
                    true
                }
                R.id.menu_refresh -> {
                    viewModel.onRefreshMenuClicked()
                    true
                }
                else -> false
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        setupRefreshLayout()
        setupFab()
    }

    override fun onViewModelSetup() {
        with(viewModel) {
            snackbarMessage.observeEvent {
                showSnackbar(it)
            }
            openTaskEvent.observeEvent {
                openTaskDetails(it)
            }
            newTaskEvent.observeEvent {
                navigateToAddNewTask()
            }
        }
    }

    private fun onFilterMenuClicked() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.onMenuItemClicked(it.itemId)

                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
            null,
            resources.getString(R.string.add_task)
        )
            .navigate()
    }

    private fun openTaskDetails(taskId: String) {
        TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(taskId)
            .navigate()
    }

    private fun setupRefreshLayout() {
        refresh_layout.run {
            setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = tasks_list
        }
    }
}
