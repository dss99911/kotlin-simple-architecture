package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.androidlibrary.extension.ctx
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class TaskDetailFragmentTest : TodoFragmentTest<TaskDetailFragment>() {
    override val fragmentClass: Class<TaskDetailFragment> = TaskDetailFragment::class.java

    val repo: TaskRepository by inject()

    @Before
    fun before() = runBlockingTest {
        repo.deleteAllTasks()
    }

    @Test
    fun init_active() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()

        //WHEN launch
        launchFragment(bundle)

        //THEN show title, description
        assertIdNotChecked(R.id.task_detail_complete)
        assertTextDisplayed(TaskSamples.sample1Active.title)
        assertTextDisplayed(TaskSamples.sample1Active.description)
    }

    @Test
    fun init_completed() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample2Completed)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample2Completed.id).build().toBundle()

        //WHEN launch
        launchFragment(bundle)

        //THEN show title, description
        assertIdChecked(R.id.task_detail_complete)
        assertTextDisplayed(TaskSamples.sample2Completed.title)
        assertTextDisplayed(TaskSamples.sample2Completed.description)
    }

    @Test
    fun init_error() = runBlockingTest {
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()

        //WHEN launch
        launchFragment(bundle)

        //THEN show nothing
        assertIdNotDisplayed(R.id.task_detail_title)

    }

    @Test
    fun onClickEdit() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()
        launchFragment(bundle)

        //WHEN click edit
        performClickById(R.id.fab_edit_task)

        //THEN go to edit page
        assertNavigateDirection(
            TaskDetailFragmentDirections
                .actionTaskDetailFragmentToAddEditTaskFragment(
                    TaskSamples.sample1Active.id,
                    ctx.getString(R.string.edit_task)
                )
        )
    }


    @Test
    fun onResume() = runBlockingTest {
        val TITLE_TEST = "resume test"

        //GIVEN task changed
        repo.saveTask(TaskSamples.sample1Active)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()
        val fragmentScenario = launchFragment(bundle)
        repo.saveTask(TaskSamples.sample1Active.apply {
            title = TITLE_TEST
        })

        //WHEN resume
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
        fragmentScenario.moveToState(Lifecycle.State.RESUMED)

        //THEN updated
        assertTextDisplayed(TITLE_TEST)
    }

    @Test
    fun onSwipeDown() = runBlockingTest {
        val TITLE_TEST = "resume test"

        //GIVEN task changed
        repo.saveTask(TaskSamples.sample1Active)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()
        launchFragment(bundle)
        repo.saveTask(TaskSamples.sample1Active.apply {
            title = TITLE_TEST
        })

        //WHEN swipe down
        onView(withId(R.id.refresh_layout)).perform(ViewActions.swipeDown())

        //THEN updated
        assertTextDisplayed(TITLE_TEST)
    }

    @Test
    fun onCheckCompleted() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        val bundle =
            TaskDetailFragmentArgs.Builder(TaskSamples.sample1Active.id).build().toBundle()
        launchFragment(bundle)

        //WHEN checked
        performClickById(R.id.task_detail_complete)

        //THEN task changed
        assertIdChecked(R.id.task_detail_complete)
        assertThat(repo.getTask(TaskSamples.sample1Active.id)!!.isCompleted).isTrue()
    }

}