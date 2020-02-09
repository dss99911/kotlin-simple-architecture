package com.example.android.architecture.blueprints.todoapp.tasks

import android.view.Gravity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TasksActivity
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth
import kim.jeonghyeon.androidtesting.BaseActivityTest
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class TasksActivityTest : BaseActivityTest<TasksActivity>() {
    override val activityClass: Class<TasksActivity> = TasksActivity::class.java

    val repo: TaskRepository by inject()

    @Before
    fun before() = runBlockingTest {
        repo.deleteAllTasks()
    }

    @Test
    fun createTask() {
        // start up Tasks screen
        launchActivity()

        // Click on the "+" button, add details, and save
        performClickById(R.id.fab_add_task)
        performTypeText(R.id.add_task_title, "title123")
        performTypeText(R.id.add_task_description, "description123")
        performClickById(R.id.fab_save_task)

        // Then verify task is displayed on screen
        onView(withText("title123")).check(matches(isDisplayed()))
    }

    @Test
    fun editTask() = runBlockingTest {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list and verify that all the data is correct
        performClickByText(TaskSamples.sample1Active.titleForList)
        assertIdMatchedText(R.id.task_detail_title, TaskSamples.sample1Active.title)
        assertIdMatchedText(R.id.task_detail_description, TaskSamples.sample1Active.description)
        assertIdNotChecked(R.id.task_detail_complete)

        // Click on the edit button, edit, and save
        performClickById(R.id.fab_edit_task)
        performReplaceText(R.id.add_task_title, TaskSamples.sample2Completed.title)
        performReplaceText(R.id.add_task_description, TaskSamples.sample2Completed.description)
        performClickById(R.id.fab_save_task)

        // Verify task is displayed on screen in the task detail.
        assertTextDisplayed(TaskSamples.sample2Completed.title)
        assertTextDisplayed(TaskSamples.sample2Completed.description)
        assertTextNotDisplayed(TaskSamples.sample1Active.title)
        assertTextNotDisplayed(TaskSamples.sample1Active.description)

        // Verify task is displayed on screen in the task list.
        pressBack()
        assertTextDisplayed(TaskSamples.sample2Completed.titleForList)
        assertTextNotDisplayed(TaskSamples.sample1Active.titleForList)
    }

    @Test
    fun createOneTask_deleteTask() {
        // start up Tasks screen
        launchActivity()

        // Add active task
        performClickById(R.id.fab_add_task)
        performTypeText(R.id.add_task_title, "TITLE1")
        performTypeText(R.id.add_task_description, "DESCRIPTION")
        performClickById(R.id.fab_save_task)

        // Open it in details view
        performClickByText("TITLE1")
        // Click delete task in menu
        performClickById(R.id.menu_delete)

        // Verify it was deleted
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_all)
        assertTextNotDisplayed("TITLE1")
    }

    @Test
    fun createTwoTasks_deleteOneTask() = runBlockingTest {
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Open the second task in details view
        performClickByText(TaskSamples.sample2Completed.titleForList)
        // Click delete task in menu
        performClickById(R.id.menu_delete)

        // Verify only one task was deleted
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_all)
        assertTextNotDisplayed(TaskSamples.sample2Completed.titleForList)
        assertTextDisplayed(TaskSamples.sample1Active.titleForList)
    }

    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() = runBlockingTest {
        // Add 1 active task
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample1Active.titleForList)

        // Click on the checkbox in task details screen
        performClickById(R.id.task_detail_complete)

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as completed
        onView(
            allOf(
                withId(R.id.complete),
                hasSibling(withText(TaskSamples.sample1Active.titleForList))
            )
        ).check(matches(isChecked()))
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() = runBlockingTest {
        // Add 1 completed task
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample2Completed.titleForList)
        // Click on the checkbox in task details screen
        performClickById(R.id.task_detail_complete)

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        onView(
            allOf(
                withId(R.id.complete),
                hasSibling(withText(TaskSamples.sample2Completed.titleForList))
            )
        ).check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreen_taskIsActiveInList() = runBlockingTest {
        // Add 1 active task
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample1Active.titleForList)

        // Click on the checkbox in task details screen
        performClickById(R.id.task_detail_complete)

        // Click again to restore it to original state
        performClickById(R.id.task_detail_complete)

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as completed
        onView(
            allOf(
                withId(R.id.complete),
                hasSibling(withText(TaskSamples.sample1Active.titleForList))
            )
        ).check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() = runBlockingTest {
        // Add 1 completed task
        repo.saveTask(TaskSamples.sample2Completed)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample2Completed.titleForList)

        // Click on the checkbox in task details screen
        performClickById(R.id.task_detail_complete)

        // Click again to restore it to original state
        performClickById(R.id.task_detail_complete)

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        onView(
            allOf(
                withId(R.id.complete),
                hasSibling(withText(TaskSamples.sample2Completed.titleForList))
            )
        ).check(matches(isChecked()))
    }


    @Test
    fun tasksFragment_onClickMenu_clearAll() = runBlockingTest {
        //GIVEN 1 task
        repo.saveTask(TaskSamples.sample5completed)
        repo.saveTask(TaskSamples.sample6completed)

        //WHEN clear completed
        launchActivity()

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        performClickByText(R.string.menu_clear)

        //THEN shows no task layout
        assertIdDisplayed(R.id.noTasks)
    }

    @Test
    fun tasksFragment_onClickMenu_refresh() = runBlockingTest {
        //GIVEN 0 task
        launchActivity()

        assertIdDisplayed(R.id.noTasks)

        //WHEN refresh
        repo.saveTask(TaskSamples.sample5completed)
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        performClickByText(R.string.refresh)

        //THEN shows no task layout
        assertTextDisplayed(TaskSamples.sample5completed.titleForList)
    }

    @Test
    fun tasksFragment_onClickMenu_filter() = runBlockingTest {
        //when click filter
        launchActivity()
        performClickById(R.id.menu_filter)

        //THEN shows filters
        assertTextDisplayed(R.string.nav_all)
        assertTextDisplayed(R.string.nav_active)
        assertTextDisplayed(R.string.nav_completed)
    }


    @Test
    fun tasksFragment_onClickMenu_filter_all() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter all
        launchActivity()
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_all)

        //THEN shows all
        list.forEach { assertTextDisplayed(it.titleForList) }
    }

    @Test
    fun tasksFragment_onClickMenu_filter_active() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launchActivity()
        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_active)

        //THEN shows active only
        assertTextDisplayed(TaskSamples.sample1Active.titleForList)
        assertTextNotDisplayed(TaskSamples.sample2Completed.titleForList)
        assertTextNotDisplayed(TaskSamples.sample2_2Completed.titleForList)
    }

    @Test
    fun tasksFragment_onClickMenu_filter_completed() = runBlockingTest {
        //GIVEN 2 completed task 1 active task
        val list = listOf(
            TaskSamples.sample1Active,
            TaskSamples.sample2Completed,
            TaskSamples.sample2_2Completed
        )

        list.forEach { repo.saveTask(it) }

        //when click filter active
        launchActivity()

        performClickById(R.id.menu_filter)
        performClickByText(R.string.nav_completed)

        //THEN shows completed only
        scrollRecyclerView(R.id.tasks_list, 2)
        assertTextNotDisplayed(TaskSamples.sample1Active.titleForList)
        assertTextDisplayed(TaskSamples.sample2Completed.titleForList)
        assertTextDisplayed(TaskSamples.sample2_2Completed.titleForList)
    }

    @Test
    fun tasksFragment_onClickOverFlowMenu() {
        launchActivity()

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        assertTextDisplayed(R.string.menu_clear)
        assertTextDisplayed(R.string.refresh)
    }


    @Test
    fun detailFragment_onClickDelete() = runBlockingTest {
        //GIVEN task
        repo.saveTask(TaskSamples.sample1Active)
        launchActivity()
        performClickByText(TaskSamples.sample1Active.titleForList)

        //WHEN click delete
        performClickById(R.id.menu_delete)

        //THEN go back
        Truth.assertThat(repo.getTasks()).hasSize(0)
        assertIdDisplayed(R.id.tasks_container)
    }


    @Test
    fun drawerNavigationFromTasksToStatistics() {
        // start up Tasks screen
        launchActivity()

        goStatPage()

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start tasks screen.
        performClickByText(R.string.list_title)

        // Check that tasks screen was opened.
        assertIdDisplayed(R.id.tasks_container)
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

        goStatPage()

        // Then check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.

        // When the drawer is opened
        clickToolbarNavigationIcon(R.id.toolbar)

        // Then check that the drawer is open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun taskDetailScreen_doubleUIBackButton() = runBlockingTest {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample1Active.titleForList)

        // Click on the edit task button
        performClickById(R.id.fab_edit_task)

        // Confirm that if we click "<-" once, we end up back at the task details page
        clickToolbarNavigationIcon(R.id.toolbar)

        assertIdDisplayed(R.id.task_detail_title)

        // Confirm that if we click "<-" a second time, we end up back at the home screen
        clickToolbarNavigationIcon(R.id.toolbar)

        assertIdDisplayed(R.id.tasks_container)
    }

    @Test
    fun taskDetailScreen_doubleBackButton() = runBlockingTest {
        repo.saveTask(TaskSamples.sample1Active)

        // start up Tasks screen
        launchActivity()

        // Click on the task on the list
        performClickByText(TaskSamples.sample1Active.titleForList)
        // Click on the edit task button
        performClickById(R.id.fab_edit_task)

        // Confirm that if we click back once, we end up back at the task details page
        pressBack()
        assertIdDisplayed(R.id.task_detail_title)

        // Confirm that if we click back a second time, we end up back at the home screen
        pressBack()
        assertIdDisplayed(R.id.tasks_container)
    }

    private fun goStatPage() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start statistics screen.
        performClickByText(R.string.statistics_title)

        // Check that statistics screen was opened.
        assertIdDisplayed(R.id.statistics)
    }
}