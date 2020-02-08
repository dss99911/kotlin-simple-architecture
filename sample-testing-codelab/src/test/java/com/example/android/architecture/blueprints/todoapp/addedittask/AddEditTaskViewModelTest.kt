package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.data.source.TaskRepository
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.testing.BaseViewModelTest
import kim.jeonghyeon.testing.awaitData
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.get
import org.koin.test.inject
import org.mockito.Mockito.spy

class AddEditTaskViewModelTest : BaseViewModelTest() {

    val repo: TaskRepository by inject()

    @Test
    fun init_add() = runBlockingTest {
        //WHEN add case
        val viewModel1 = initViewModel(null)

        //THEN new task
        assertThat(viewModel1.task.awaitData()).isNotNull()
    }

    @Test
    fun init_edit() = runBlockingTest {
        //GIVEN same task
        repo.saveTask(TaskSamples.sample1Active)

        //WHEN edit case
        val viewModel3 = initViewModel(TaskSamples.sample1Active.id)

        //THEN
        assertThat(viewModel3.task.awaitData()).isNotNull()
        assertThat(viewModel3.task.awaitData().id).isEqualTo(TaskSamples.sample1Active.id)
    }

    @Test
    fun init_editButNoTask() = runBlockingTest {
        //GIVEN no task

        //WHEN edit case
        val viewModel2 = initViewModel(TaskSamples.sample1Active.id)

        //THEN new task
        assertThat(viewModel2.task.awaitData()).isNotNull()
        assertThat(viewModel2.task.awaitData().id).isNotEqualTo(TaskSamples.sample1Active.id)


    }


    @Test
    fun onClickFAB() = runBlockingTest {
        //given
        val viewModel = initViewModel(null)

        //when click
        viewModel.onClickFAB()

        //then task added, navigate to home
        assertThat(repo.getTasks()).hasSize(1)
        viewModel.verifyNavigateUp()
    }

    private fun initViewModel(taskId: String?): AddEditTaskViewModel {
        val viewModel = get<AddEditTaskViewModel> {
            parametersOf(AddEditTaskFragmentArgs.Builder(taskId, "test title").build())
        }.let { spy(it) }

        //THEN new task
        assertThat(viewModel.task.awaitData()).isNotNull()
        return viewModel
    }
}