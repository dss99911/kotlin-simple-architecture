/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.statistics

import com.balancehero.example.androidtesting.BaseViewModelTest
import com.balancehero.example.androidtesting.awaitData
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

class StatisticsViewModelTest : BaseViewModelTest() {

    val viewModel by inject<StatisticsViewModel>()
    val repo by inject<TaskRepository>()

    @Test
    fun onResume() = runBlockingTest {
        //GIVEN tasks
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)

        //WHEN
        viewModel.onResume()

        //THEN
        assertThat(viewModel.statusResult.awaitData()).isInstanceOf(StatsResult::class.java)

    }

}