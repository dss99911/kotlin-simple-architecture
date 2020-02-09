package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.extension.ctx
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

class AddEditTaskFragmentTest : TodoFragmentTest<AddEditTaskFragment>() {
    override val fragmentClass: Class<AddEditTaskFragment>
        get() = AddEditTaskFragment::class.java

    val repo: TaskRepository by inject()

    @Test
    fun init_add() = runBlockingTest {
        //GIVEN null args
        val bundle = AddEditTaskFragmentArgs.Builder(null, ctx.getString(R.string.add_task)).build()
            .toBundle()

        //WHEN
        launchFragment(bundle)

        //THEN no title description shown
        assertHintDisplayed(ctx.getString(R.string.title_hint))
        assertHintDisplayed(ctx.getString(R.string.description_hint))
    }

    @Test
    fun init_edit() = runBlockingTest {
        //GIVEN task args
        repo.saveTask(TaskSamples.sample1Active)
        val bundle = AddEditTaskFragmentArgs.Builder(
            TaskSamples.sample1Active.id,
            ctx.getString(R.string.edit_task)
        ).build().toBundle()

        //WHEN
        launchFragment(bundle)

        //THEN title description shown
        assertTextDisplayed(TaskSamples.sample1Active.title)
        assertTextDisplayed(TaskSamples.sample1Active.description)
    }

    @Test
    fun onClickFAB_emptyTitleTask() = runBlockingTest {
        //GIVEN empty title
        repo.saveTask(TaskSamples.sample3TitleEmpty)
        val bundle = AddEditTaskFragmentArgs.Builder(
            TaskSamples.sample3TitleEmpty.id,
            ctx.getString(R.string.edit_task)
        ).build().toBundle()
        launchFragment(bundle)

        //WHEN
        performClickById(R.id.fab_save_task)

        //THEN show error and also keep the page
        assertTextDisplayed(TaskSamples.sample3TitleEmpty.description)
    }

    @Test
    fun onClickFAB_emptyDescriptionTask() = runBlockingTest {
        //GIVEN empty title
        repo.saveTask(TaskSamples.sample4DescriptionEmpty)
        val bundle = AddEditTaskFragmentArgs.Builder(
            TaskSamples.sample4DescriptionEmpty.id,
            ctx.getString(R.string.edit_task)
        ).build().toBundle()
        launchFragment(bundle)

        //WHEN
        performClickById(R.id.fab_save_task)

        //THEN show error and also keep the page
        assertTextDisplayed(TaskSamples.sample4DescriptionEmpty.title)
    }

    @Test
    fun onClickFAB_notEmptyTask() = runBlockingTest {
        //GIVEN emptyDescription
        repo.saveTask(TaskSamples.sample1Active)
        val bundle = AddEditTaskFragmentArgs.Builder(
            TaskSamples.sample1Active.id,
            ctx.getString(R.string.edit_task)
        ).build().toBundle()
        launchFragment(bundle)

        //WHEN
        performClickById(R.id.fab_save_task)

        //THEN navigateUp
        assertNavigateUp()
    }
}