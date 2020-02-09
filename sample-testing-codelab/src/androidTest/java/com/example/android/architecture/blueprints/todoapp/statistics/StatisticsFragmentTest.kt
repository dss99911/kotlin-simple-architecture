package com.example.android.architecture.blueprints.todoapp.statistics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class StatisticsFragmentTest : TodoFragmentTest<StatisticsFragment>() {
    override val fragmentClass: Class<StatisticsFragment> = StatisticsFragment::class.java

    val repo: TaskRepository by inject()

    @Before
    fun before() = runBlockingTest {
        repo.deleteAllTasks()
    }

    @Test
    fun activeOnly() = runBlockingTest {
        //GIVEN active tasks
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample1_2Active)

        //WHEN
        launchFragment()

        //THEN
        val expectedActiveTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_active_tasks, 100f)
        val expectedCompletedTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_completed_tasks, 0f)
        assertTextDisplayed(expectedActiveTaskText)
        assertTextDisplayed(expectedCompletedTaskText)
    }

    @Test
    fun completedOnly() = runBlockingTest {
        //GIVEN active tasks
        repo.saveTask(TaskSamples.sample2Completed)
        repo.saveTask(TaskSamples.sample2_2Completed)

        //WHEN
        launchFragment()

        //THEN
        val expectedActiveTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_active_tasks, 0f)
        val expectedCompletedTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_completed_tasks, 100f)
        assertTextDisplayed(expectedActiveTaskText)
        assertTextDisplayed(expectedCompletedTaskText)
    }

    @Test
    fun both() = runBlockingTest {
        //GIVEN active tasks
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)

        //WHEN
        launchFragment()

        //THEN
        val expectedActiveTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_active_tasks, 50f)
        val expectedCompletedTaskText = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.statistics_completed_tasks, 50f)
        assertTextDisplayed(expectedActiveTaskText)
        assertTextDisplayed(expectedCompletedTaskText)
    }
}