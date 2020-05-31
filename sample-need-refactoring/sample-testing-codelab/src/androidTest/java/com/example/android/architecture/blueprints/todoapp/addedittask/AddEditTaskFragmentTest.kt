package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TodoFragmentTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidtesting.EspressoUtil
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
        launchWith(null)

        //THEN no title description shown
        assertHintDisplayed(ctx.getString(R.string.title_hint))
        assertHintDisplayed(ctx.getString(R.string.description_hint))
    }

    @Test
    fun init_edit() = runBlockingTest {
        //GIVEN task args
        repo.saveTask(TaskSamples.sample1Active)

        //WHEN
        launchWith(TaskSamples.sample1Active)

        //THEN title description shown
        isTitleAndDescriptionDisplayed(TaskSamples.sample1Active)
    }

    @Test
    fun onClickFAB_emptyTitleTask() = runBlockingTest {
        //GIVEN empty title
        repo.saveTask(TaskSamples.sample3TitleEmpty)
        launchWith(TaskSamples.sample3TitleEmpty)

        //WHEN
        clickSave()

        //THEN show error and also keep the page
        isShown()
    }

    @Test
    fun onClickFAB_emptyDescriptionTask() = runBlockingTest {
        //GIVEN empty title
        repo.saveTask(TaskSamples.sample4DescriptionEmpty)
        launchWith(TaskSamples.sample4DescriptionEmpty)

        //WHEN
        clickSave()

        //THEN show error and also keep the page
        isShown()
    }

    @Test
    fun onClickFAB_notEmptyTask() = runBlockingTest {
        //GIVEN emptyDescription
        repo.saveTask(TaskSamples.sample1Active)
        launchWith(TaskSamples.sample1Active)

        //WHEN
        clickSave()

        //THEN navigateUp
        assertNavigateUp()
    }

    private fun launchWith(task: Task?) {
        val bundle =
            AddEditTaskFragmentArgs.Builder(task?.id, ctx.getString(R.string.add_task)).build()
                .toBundle()

        //WHEN
        launchFragment(bundle)
    }


    companion object : EspressoUtil {
        fun typeTitleAndDescription(task: Task) {
            performTypeText(R.id.add_task_title, task.title)
            performTypeText(R.id.add_task_description, task.description)
        }

        fun replaceTitleAndDescription(task: Task) {
            performReplaceText(R.id.add_task_title, task.title)
            performReplaceText(R.id.add_task_description, task.description)
        }

        fun clickSave() {
            performClickById(R.id.fab_save_task)
        }

        fun isTitleAndDescriptionDisplayed(task: Task) {
            assertTextDisplayed(task.title)
            assertTextDisplayed(task.description)
        }

        fun isShown() {
            assertIdDisplayed(R.id.add_task_title)
        }
    }
}