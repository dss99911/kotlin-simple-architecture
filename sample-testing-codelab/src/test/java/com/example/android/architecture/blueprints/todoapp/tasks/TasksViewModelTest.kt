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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.testing.BaseViewModelTest
import kim.jeonghyeon.testing.await
import kim.jeonghyeon.testing.awaitData
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

/**
 * Unit tests for the implementation of [TasksViewModel]
 */
class TasksViewModelTest : BaseViewModelTest() {

    val viewModel: TasksViewModel by inject()

    val repo: TaskRepository by inject()

    @Before
    fun before() {
        runBlockingTest {
            //given 1 active, 1 completed task
            repo.saveTask(TaskSamples.sample1Active)
            repo.saveTask(TaskSamples.sample2Completed)
        }
    }

    @Test
    fun onCreate() {
        //when init

        //then
        assertFilter(R.string.label_all, R.string.no_tasks_all, R.drawable.logo_no_fill, true)
        assertItemSize(2)
    }

    @Test
    fun onClearMenuClicked() {
        //given
        onCreate()

        //when
        viewModel.onClearMenuClicked()

        //then
        assertItemSize(1)
        assertSnackbar(R.string.completed_tasks_cleared)
    }

    @Test
    fun onRefreshMenuClicked() {
        //when
        viewModel.onRefreshMenuClicked()

        //then
        assertItemSize(2)
    }

    @Test
    fun onMenuItemClicked_active() {
        //when active
        viewModel.onMenuItemClicked(R.id.active)

        //then
        assertFilter(
            R.string.label_active,
            R.string.no_tasks_active,
            R.drawable.ic_check_circle_96dp,
            false
        )
        assertItemSize(1)
    }

    @Test
    fun onMenuItemClicked_completed() {
        //when completed
        viewModel.onMenuItemClicked(R.id.completed)

        //then
        assertFilter(
            R.string.label_completed,
            R.string.no_tasks_completed,
            R.drawable.ic_verified_user_96dp,
            false
        )
        assertItemSize(1)
    }

    @Test
    fun onMenuItemClicked_all() {
        //when all
        viewModel.onMenuItemClicked(R.id.all)

        //then
        assertFilter(R.string.label_all, R.string.no_tasks_all, R.drawable.logo_no_fill, true)
        assertItemSize(2)
    }


    private fun assertFilter(
        @StringRes filteringLabelString: Int, @StringRes noTasksLabelString: Int,
        @DrawableRes noTaskIconDrawable: Int, tasksAddVisible: Boolean
    ) {
        assertThat(viewModel.currentFilteringLabel.value).isEqualTo(filteringLabelString)
        assertThat(viewModel.noTasksLabel.value).isEqualTo(noTasksLabelString)
        assertThat(viewModel.noTaskIconRes.value).isEqualTo(noTaskIconDrawable)
        assertThat(viewModel.tasksAddViewVisible.value).isEqualTo(tasksAddVisible)
    }

    private fun assertSnackbar(@StringRes stringResId: Int) {
        assertThat(viewModel.eventSnackbar.await()).isEqualTo(stringResId)
    }

    private fun assertItemSize(size: Int) {
        assertThat(viewModel.items.awaitData().size).isEqualTo(size)
    }
}
