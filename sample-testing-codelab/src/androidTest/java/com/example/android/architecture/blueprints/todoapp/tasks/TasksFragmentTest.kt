package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject


/**
 * Integration test for the Task List screen.
 */
// TODO - Use FragmentScenario, see: https://github.com/android/android-test/issues/291
class TasksFragmentTest : TodoFragmentTest<TasksFragment>() {
    override val fragmentClass = TasksFragment::class.java

    private val repo: TaskRepository by inject()

    @Before
    fun before() = runBlockingTest {
        repo.deleteAllTasks()
    }

    @Test
    fun init_size1() = runBlockingTest {
        //GIVEN 1 task
        repo.saveTask(TaskSamples.sample5completed)

        //WHEN init
        launchFragment()


        //THEN has title of the task
        assertTextDisplayed(TaskSamples.sample5completed.titleForList)
    }

    @Test
    fun init_size2() = runBlockingTest {
        //GIVEN 2 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN init
        launchFragment()

        //THEN has title of the task
        assertTextDisplayed(TaskSamples.sample5completed.titleForList)
        assertTextDisplayed(TaskSamples.sample6completed.titleForList)
    }

    @Test
    fun init_empty() = runBlockingTest {
        //GIVEN 0 task

        //WHEN init
        launchFragment()

        //THEN shows no task layout
        assertIdDisplayed(R.id.noTasks)
    }

    @Test
    fun onSwipeDown() = runBlockingTest {
        //GIVEN data is different
        repo.saveTask(TaskSamples.sample1Active)
        launchFragment()
        assertTextDisplayed(TaskSamples.sample1Active.titleForList)
        getFragment().viewModel.items.value = Resource.Success(emptyList())

        assertTextNotDisplayed(TaskSamples.sample1Active.titleForList)
        //WHEN
        onView(withId(R.id.refresh_layout)).perform(swipeDown())


        //THEN
        assertTextDisplayed(TaskSamples.sample1Active.titleForList)
    }

    @Test
    fun onItemClick() = runBlockingTest {
        //GIVEN 1task
        repo.saveTask(TaskSamples.sample5completed)
        launchFragment()

        //WHEN click
        performClickByText(TaskSamples.sample5completed.titleForList)

        //THEN redirect to detail fragment
        assertNavigateDirection(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(
                "id5"
            )
        )

    }

    @Test
    fun onAddClick() = runBlockingTest {
        //GIVEN
        launchFragment()

        //WHEN click
        performClickById(R.id.fab_add_task)

        //THEN go to add page
        assertNavigateDirection(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                "New Task"
            )
        )
    }
}
