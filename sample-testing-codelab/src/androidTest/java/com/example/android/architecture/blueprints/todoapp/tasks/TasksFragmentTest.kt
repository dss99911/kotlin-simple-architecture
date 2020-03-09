package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidtesting.EspressoUtil
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers
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
        isItemDisplayed(TaskSamples.sample5completed)
    }

    @Test
    fun init_size2() = runBlockingTest {
        //GIVEN 2 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN init
        launchFragment()

        //THEN has title of the task
        isItemDisplayed(TaskSamples.sample5completed)
        isItemDisplayed(TaskSamples.sample6completed)
    }

    @Test
    fun init_empty() = runBlockingTest {
        //GIVEN 0 task

        //WHEN init
        launchFragment()

        //THEN shows no task layout
        hasNoTask()
    }

    @Test
    fun onSwipeDown() = runBlockingTest {
        //GIVEN data is different
        repo.saveTask(TaskSamples.sample1Active)
        launchFragment()

        isItemDisplayed(TaskSamples.sample1Active)

        //set items ui empty
        getFragment().viewModel.items.value = Resource.Success(emptyList())

        isItemNotDisplayed(TaskSamples.sample1Active)

        //WHEN
        swipeDown()

        //THEN
        isItemDisplayed(TaskSamples.sample1Active)
    }

    @Test
    fun onItemClick() = runBlockingTest {
        //GIVEN 1task
        repo.saveTask(TaskSamples.sample5completed)
        launchFragment()

        //WHEN click
        clickTaskItem(TaskSamples.sample5completed)

        //THEN redirect to detail fragment
        assertNavigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(
                TaskSamples.sample5completed.id
            )
        )

    }

    @Test
    fun onAddClick() = runBlockingTest {
        //GIVEN
        launchFragment()

        //WHEN click
        clickAdd()

        //THEN go to add page
        assertNavigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                "New Task"
            )
        )
    }

    companion object : EspressoUtil {
        fun clickAdd() {
            performClickById(R.id.fab_add_task)
        }

        fun clickTaskItem(task: Task) {
            performClickByText(task.titleForList)
        }

        fun isItemDisplayed(task: Task) {
            assertTextDisplayed(task.titleForList)
        }

        fun isItemNotDisplayed(task: Task) {
            assertTextNotDisplayed(task.titleForList)
        }

        fun clickFilterAll() {
            clickMenuFilter()
            performClickByText(R.string.nav_all)
        }

        fun clickFilterActive() {
            clickMenuFilter()
            performClickByText(R.string.nav_active)
        }

        fun clickFilterCompleted() {
            clickMenuFilter()
            performClickByText(R.string.nav_completed)
        }

        fun isCompleted(task: Task) {
            onView(
                Matchers.allOf(
                    withId(R.id.complete),
                    ViewMatchers.hasSibling(ViewMatchers.withText(task.titleForList))
                )
            ).check(ViewAssertions.matches(ViewMatchers.isChecked()))
        }

        fun isActive(task: Task) {
            onView(
                Matchers.allOf(
                    withId(R.id.complete),
                    ViewMatchers.hasSibling(ViewMatchers.withText(task.titleForList))
                )
            ).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isChecked())))
        }

        fun clickMenuFilter() {
            performClickById(R.id.menu_filter)
        }

        fun clickMenuClear() {
            Espresso.openActionBarOverflowOrOptionsMenu(ctx)
            performClickByText(R.string.menu_clear)
        }

        fun clickRefresh() {
            Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
            performClickByText(R.string.refresh)
        }

        fun hasNoTask() {
            assertIdDisplayed(R.id.noTasks)
        }

        fun scroll(index: Int) {
            scrollRecyclerView(R.id.tasks_list, index)
        }

        fun isShown() {
            assertIdDisplayed(R.id.tasks_container)
        }

        fun swipeDown() {
            onView(withId(R.id.refresh_layout)).perform(ViewActions.swipeDown())
        }
    }
}
