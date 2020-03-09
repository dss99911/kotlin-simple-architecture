package com.example.android.architecture.blueprints.todoapp

import android.view.Gravity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsFragmentTest
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailFragmentTest
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragmentTest
import com.google.common.truth.Truth
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidtesting.BaseActivityTest
import kim.jeonghyeon.androidtesting.EspressoUtil
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class TasksActivityTest : BaseActivityTest<TasksActivity>() {
    override val activityClass: Class<TasksActivity> = TasksActivity::class.java

    val repo: TaskRepository by inject()

    @Before
    fun before() = runBlocking {
        repo.deleteAllTasks()
    }

    @Test
    fun createTask() {
        // start up Tasks screen
        launchActivity()

        // Click on the "+" button, add details, and save
        TasksFragmentTest.clickAdd()
        AddEditTaskFragmentTest.typeTitleAndDescription(Task("title123", "description123"))
        AddEditTaskFragmentTest.clickSave()

        // Then verify task is displayed on screen
        assertTextDisplayed("title123")
    }

    @Test
    fun editTask() = runBlocking {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list and verify that all the data is correct
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)
        TaskDetailFragmentTest.isTitleAndDescriptionDisplayed(TaskSamples.sample1Active)
        TaskDetailFragmentTest.isCompleted(false)

        // Click on the edit button, edit, and save
        TaskDetailFragmentTest.clickEdit()
        AddEditTaskFragmentTest.replaceTitleAndDescription(TaskSamples.sample2Completed)
        AddEditTaskFragmentTest.clickSave()

        // Verify task is displayed on screen in the task detail.
        TaskDetailFragmentTest.isTitleAndDescriptionDisplayed(TaskSamples.sample2Completed)
        TaskDetailFragmentTest.isTitleAndDescriptionNotDisplayed(TaskSamples.sample1Active)

        // Verify task is displayed on screen in the task list.
        pressBack()
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample2Completed)
        TasksFragmentTest.isItemNotDisplayed(TaskSamples.sample1Active)
    }

    @Test
    fun createOneTask_deleteTask() {
        // start up Tasks screen
        launchActivity()

        // Add active task
        val task = Task("TITLE1", "DESCRIPTION")
        TasksFragmentTest.clickAdd()
        AddEditTaskFragmentTest.typeTitleAndDescription(task)
        AddEditTaskFragmentTest.clickSave()

        // Open it in details view
        TasksFragmentTest.clickTaskItem(task)
        // Click delete task in menu
        TaskDetailFragmentTest.clickDelete()

        // Verify it was deleted
        TasksFragmentTest.clickFilterAll()
        TasksFragmentTest.isItemNotDisplayed(task)
    }

    @Test
    fun createTwoTasks_deleteOneTask() = runBlocking {
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Open the second task in details view
        TasksFragmentTest.clickTaskItem(TaskSamples.sample2Completed)
        // Click delete task in menu
        TaskDetailFragmentTest.clickDelete()

        // Verify only one task was deleted
        TasksFragmentTest.clickFilterAll()
        TasksFragmentTest.isItemNotDisplayed(TaskSamples.sample2Completed)
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample1Active)
    }

    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() = runBlocking {
        // Add 1 active task
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)

        // Click on the checkbox in task details screen
        TaskDetailFragmentTest.clickCheckButton()

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as completed
        TasksFragmentTest.isCompleted(TaskSamples.sample1Active)
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() = runBlocking {
        // Add 1 completed task
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample2Completed)
        // Click on the checkbox in task details screen
        TaskDetailFragmentTest.clickCheckButton()

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        TasksFragmentTest.isActive(TaskSamples.sample2Completed)
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreen_taskIsActiveInList() = runBlocking {
        // Add 1 active task
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)

        // Click on the checkbox in task details screen
        TaskDetailFragmentTest.clickCheckButton()

        // Click again to restore it to original state
        TaskDetailFragmentTest.clickCheckButton()

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        TasksFragmentTest.isActive(TaskSamples.sample1Active)
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() = runBlocking {
        // Add 1 completed task
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample2Completed)

        // Click on the checkbox in task details screen
        TaskDetailFragmentTest.clickCheckButton()

        // Click again to restore it to original state
        TaskDetailFragmentTest.clickCheckButton()

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as completed
        TasksFragmentTest.isCompleted(TaskSamples.sample2Completed)
    }


    @Test
    fun tasksFragment_onClickMenu_clearAll() = runBlocking {
        //GIVEN 2 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN clear completed
        launchActivity()

        TasksFragmentTest.clickMenuClear()

        //THEN shows no task layout
        TasksFragmentTest.hasNoTask()
    }

    @Test
    fun tasksFragment_onClickMenu_refresh() = runBlocking {
        //GIVEN 0 task
        launchActivity()

        TasksFragmentTest.hasNoTask()

        //WHEN refresh
        repo.saveTask(TaskSamples.sample5completed)
        TasksFragmentTest.clickRefresh()

        //THEN shows the task layout
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample5completed)
    }

    @Test
    fun tasksFragment_onClickMenu_filter() = runBlocking {
        //when click filter
        launchActivity()
        TasksFragmentTest.clickMenuFilter()

        //THEN shows filters
        assertTextDisplayed(R.string.nav_all)
        assertTextDisplayed(R.string.nav_active)
        assertTextDisplayed(R.string.nav_completed)
    }


    @Test
    fun tasksFragment_onClickMenu_filter_all() = runBlocking {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter all
        launchActivity()
        TasksFragmentTest.clickFilterAll()

        //THEN shows all
        list.forEach { assertTextDisplayed(it.titleForList) }
    }

    @Test
    fun tasksFragment_onClickMenu_filter_active() = runBlocking {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launchActivity()
        TasksFragmentTest.clickFilterActive()

        //THEN shows active only
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample1Active)
        TasksFragmentTest.isItemNotDisplayed(TaskSamples.sample2Completed)
        TasksFragmentTest.isItemNotDisplayed(TaskSamples.sample2_2Completed)
    }

    @Test
    fun tasksFragment_onClickMenu_filter_completed() = runBlocking {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launchActivity()

        TasksFragmentTest.clickFilterCompleted()

        //THEN shows completed only
        TasksFragmentTest.scroll(2)
        TasksFragmentTest.isItemNotDisplayed(TaskSamples.sample1Active)
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample2Completed)
        TasksFragmentTest.isItemDisplayed(TaskSamples.sample2_2Completed)
    }

    @Test
    fun tasksFragment_onClickOverFlowMenu() {
        launchActivity()

        openActionBarOverflowOrOptionsMenu(ctx)

        assertTextDisplayed(R.string.menu_clear)
        assertTextDisplayed(R.string.refresh)
    }


    @Test
    fun detailFragment_onClickDelete() = runBlocking {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        launchActivity()
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)

        //WHEN click delete
        TaskDetailFragmentTest.clickDelete()

        //THEN go back
        Truth.assertThat(repo.getTasks()).hasSize(0)
        TasksFragmentTest.isShown()
    }


    @Test
    fun drawerNavigationFromTasksToStatistics() {
        // start up Tasks screen
        launchActivity()

        clickStatistics()
        StatisticsFragmentTest.isShown()

        clickTaskList()

        // Check that tasks screen was opened.
        TasksFragmentTest.isShown()
    }

    @Test
    fun tasksScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen
        launchActivity()

        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // Open Drawer
        clickToolbarNavigationIcon(R.id.toolbar)

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun statsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen

        launchActivity()

        clickStatistics()
        StatisticsFragmentTest.isShown()

        // Then check that left drawer is closed at startup
        isDrawerClose()

        // When the drawer is opened
        clickToolbarNavigationIcon(R.id.toolbar)

        // Then check that the drawer is open
        isDrawerOpened()
    }

    @Test
    fun taskDetailScreen_doubleUIBackButton() = runBlocking {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)

        // Click on the edit task button
        TaskDetailFragmentTest.clickEdit()

        // Confirm that if we click "<-" once, we end up back at the task details page
        clickToolbarNavigationIcon(R.id.toolbar)

        TaskDetailFragmentTest.isShown()

        // Confirm that if we click "<-" a second time, we end up back at the home screen
        clickToolbarNavigationIcon(R.id.toolbar)

        TasksFragmentTest.isShown()
    }

    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        TasksFragmentTest.clickTaskItem(TaskSamples.sample1Active)
        // Click on the edit task button
        TaskDetailFragmentTest.clickEdit()

        // Confirm that if we click back once, we end up back at the task details page
        pressBack()
        TaskDetailFragmentTest.isShown()

        // Confirm that if we click back a second time, we end up back at the home screen
        pressBack()
        TasksFragmentTest.isShown()
    }

    companion object : EspressoUtil {
        fun openDrawer() {
            onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
                .perform(DrawerActions.open()) // Open Drawer
        }

        fun isDrawerClose() {
            onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
        }

        fun isDrawerOpened() {
            onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START))) // Left Drawer should be closed.
        }

        fun clickStatistics() {
            openDrawer()

            // Start statistics screen.
            performClickByText(R.string.statistics_title)
        }

        fun clickTaskList() {
            openDrawer()
            performClickByText(R.string.list_title)
        }
    }
}