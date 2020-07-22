package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidtesting.EspressoUtil
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

        //WHEN launch
        launchWith(TaskSamples.sample1Active)

        //THEN show title, description
        isCompleted(false)
        isTitleAndDescriptionDisplayed(TaskSamples.sample1Active)
    }

    @Test
    fun init_completed() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample2Completed)

        //WHEN launch
        launchWith(TaskSamples.sample2Completed)

        //THEN show title, description
        isCompleted(true)
        isTitleAndDescriptionDisplayed(TaskSamples.sample2Completed)
    }

    @Test
    fun init_error() = runBlockingTest {
        //WHEN launch
        launchWith(TaskSamples.sample1Active)

        //THEN show nothing
        isNotShown()
    }

    @Test
    fun onClickEdit() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        launchWith(TaskSamples.sample1Active)

        //WHEN click edit
        clickEdit()

        //THEN go to edit page
        assertNavigate(
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

        val fragmentScenario = launchWith(TaskSamples.sample1Active)

        val changedTask = TaskSamples.sample1Active.apply {
            title = TITLE_TEST
        }
        repo.saveTask(changedTask)

        //WHEN resume
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
        fragmentScenario.moveToState(Lifecycle.State.RESUMED)

        //THEN updated
        isTitleAndDescriptionDisplayed(changedTask)
    }

    @Test
    fun onSwipeDown() = runBlockingTest {
        val TITLE_TEST = "resume test"

        //GIVEN task changed
        repo.saveTask(TaskSamples.sample1Active)
        launchWith(TaskSamples.sample1Active)

        val changedTask = TaskSamples.sample1Active.apply {
            title = TITLE_TEST
        }
        repo.saveTask(changedTask)

        //WHEN swipe down

        swipeDown()

        //THEN updated
        isTitleAndDescriptionDisplayed(changedTask)
    }

    @Test
    fun onCheckCompleted() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        launchWith(TaskSamples.sample1Active)

        //WHEN checked
        clickCheckButton()

        //THEN task changed
        isCompleted(true)
        assertThat(repo.getTask(TaskSamples.sample1Active.id)!!.isCompleted).isTrue()
    }

    private fun launchWith(task: Task): FragmentScenario<TaskDetailFragment> {
        val bundle =
            TaskDetailFragmentArgs.Builder(task.id).build().toBundle()

        //WHEN launch
        return launchFragment(bundle)
    }

    companion object : EspressoUtil {
        fun isTitleAndDescriptionDisplayed(task: Task) {
            assertIdMatchedText(R.id.task_detail_title, task.title)
            assertIdMatchedText(R.id.task_detail_description, task.description)
        }

        fun isTitleAndDescriptionNotDisplayed(task: Task) {
            assertTextNotDisplayed(task.title)
            assertTextNotDisplayed(task.description)
        }

        fun isCompleted(completed: Boolean) {
            if (completed) {
                assertIdChecked(R.id.task_detail_complete)
            } else {
                assertIdNotChecked(R.id.task_detail_complete)
            }
        }

        fun clickEdit() {
            performClickById(R.id.fab_edit_task)
        }

        fun clickDelete() {
            performClickById(R.id.menu_delete)
        }

        fun clickCheckButton() {
            performClickById(R.id.task_detail_complete)
        }

        fun isShown() {
            assertIdDisplayed(R.id.task_detail_title)
        }

        fun isNotShown() {
            assertIdNotDisplayed(R.id.task_detail_title)
        }

        fun swipeDown() {
            onView(withId(R.id.refresh_layout)).perform(ViewActions.swipeDown())
        }
    }
}