package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TasksActivity
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.Task
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
    override val clazz = TasksFragment::class.java

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
        TaskSamples.sample5completed.assertDisplayed()
    }

    @Test
    fun init_size2() = runBlockingTest {
        //GIVEN 2 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN init
        launchFragment()

        //THEN has title of the task
        TaskSamples.sample5completed.assertDisplayed()
        TaskSamples.sample6completed.assertDisplayed()
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
    fun onClickOverFlowMenu() {
        launch(TasksActivity::class.java)

        openActionBarOverflowOrOptionsMenu(getApplicationContext())

        assertTextDisplayed(R.string.menu_clear)
        assertTextDisplayed(R.string.refresh)
    }

    @Test
    fun onClickMenu_clearAll() = runBlockingTest {
        //GIVEN 1 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN clear completed
        launch(TasksActivity::class.java)

        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        performClickByText(R.string.menu_clear)

        //THEN shows no task layout
        assertIdDisplayed(R.id.noTasks)
    }

    @Test
    fun onClickMenu_refresh() = runBlockingTest {
        //GIVEN 0 task
        launch(TasksActivity::class.java)

        assertIdDisplayed(R.id.noTasks)

        //WHEN refresh
        repo.saveTask(TaskSamples.sample5completed)
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        performClickByText(R.string.refresh)

        //THEN shows no task layout
        TaskSamples.sample5completed.assertDisplayed()
    }

    @Test
    fun onClickMenu_filter() = runBlockingTest {
        //when click filter
        launch(TasksActivity::class.java)
        performClickById(R.id.menu_filter)

        //THEN shows filters
        assertTextDisplayed(R.string.nav_all)
        assertTextDisplayed(R.string.nav_active)
        assertTextDisplayed(R.string.nav_completed)
    }


    @Test
    fun onClickMenu_filter_all() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter all
        launch(TasksActivity::class.java)
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_all)

        //THEN shows all
        list.forEach { it.assertDisplayed() }
    }

    @Test
    fun onClickMenu_filter_active() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launch(TasksActivity::class.java)
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_active)

        //THEN shows active only
        TaskSamples.sample1Active.assertDisplayed()
        TaskSamples.sample2Completed.assertNotDisplayed()
        TaskSamples.sample2_2Completed.assertNotDisplayed()
    }

    @Test
    fun onClickMenu_filter_completed() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launch(TasksActivity::class.java)

        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_completed)

        //THEN shows completed only
        scrollRecyclerView(R.id.tasks_list, 2)
        TaskSamples.sample1Active.assertNotDisplayed()
        TaskSamples.sample2Completed.assertDisplayed()
        TaskSamples.sample2_2Completed.assertDisplayed()
    }

    @Test
    fun onSwipeDown() = runBlockingTest {
        //GIVEN data is different
        repo.saveTask(TaskSamples.sample1Active)
        val fragment = launchFragmentWithFragment()
        TaskSamples.sample1Active.assertDisplayed()
        fragment.viewModel.items.value = Resource.Success(emptyList())

        TaskSamples.sample1Active.assertNotDisplayed()
        //WHEN
        onView(withId(R.id.refresh_layout)).perform(swipeDown())


        //THEN
        TaskSamples.sample1Active.assertDisplayed()
    }

    @Test
    fun onItemClick() = runBlockingTest {
        //GIVEN 1task
        repo.saveTask(TaskSamples.sample5completed)
        val fragment = launchFragmentWithFragment()

        //WHEN click
        performClickByText(TaskSamples.sample5completed.titleForList)

        //THEN redirect to detail fragment
        fragment.assertNavigateDirection(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(
                "id5"
            )
        )

    }

    @Test
    fun onAddClick() = runBlockingTest {
        //GIVEN
        val fragment = launchFragmentWithFragment()

        //WHEN click
        performClickById(R.id.fab_add_task)

        //THEN go to add page
        fragment.assertNavigateDirection(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                "New Task"
            )
        )
    }

    private fun Task.assertDisplayed() {
        assertTextDisplayed(titleForList)
    }

    private fun Task.assertNotDisplayed() {
        assertTextNotDisplayed(titleForList)
    }
}
