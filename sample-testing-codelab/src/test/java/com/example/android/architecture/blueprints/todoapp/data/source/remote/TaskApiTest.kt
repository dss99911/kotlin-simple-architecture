package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.TodoKoinTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample1Active
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample2Completed
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample2_2Completed
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample3TitleEmpty
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class TaskApiTest : TodoKoinTest() {
    val api: TaskApi by inject()

    @Before
    fun before() = runBlockingTest {
        api.deleteAllTasks()
    }

    @Test
    fun getTasks_empty() = runBlockingTest {
        //when empty
        assertThat(api.getTasks()).isEmpty()
    }

    @Test
    fun getTasks_size1() = runBlockingTest {
        //when size 1
        api.saveTask(sample1Active.id, sample1Active)
        assertThat(api.getTasks()).hasSize(1)
    }

    @Test
    fun getTasks_size2() = runBlockingTest {
        //when size 2
        api.saveTask(sample1Active.id, sample1Active)
        api.saveTask(sample2Completed.id, sample2Completed)
        assertThat(api.getTasks()).hasSize(2)
    }

    @Test
    fun getTask_notExists() = runBlockingTest {
        //when not exists
        val task = api.getTask(sample1Active.id)
        //Then null
        assertThat(task).isNull()
    }

    @Test
    fun getTask_exists() = runBlockingTest {


        //when exists
        api.saveTask(sample1Active.id, sample1Active)
        //then no error occurs
        assertThat(api.getTask(sample1Active.id)).isEqualTo(sample1Active)
    }

    @Test
    fun saveTask_size1() = runBlockingTest {
        //when add
        api.saveTask(sample1Active.id, sample1Active)

        //then size 1
        assertThat(api.getTasks()).hasSize(1)
    }

    @Test
    fun saveTask_size2() = runBlockingTest {
        //when add 2 item
        api.saveTask(sample2Completed.id, sample2Completed)
        api.saveTask(sample3TitleEmpty.id, sample3TitleEmpty)

        //then 2 size increased
        assertThat(api.getTasks()).hasSize(2)
    }

    @Test
    fun saveTask_addSameTasks() = runBlockingTest {
        //when add same task id
        api.saveTask(sample1Active.id, sample1Active)
        api.saveTask(sample1Active.id, sample1Active)

        //then size 1
        assertThat(api.getTasks()).hasSize(1)
    }

    @Test
    fun completeTask() = runBlockingTest {
        //given
        api.saveTask(sample1Active.id, sample1Active)

        //when complete
        api.completeTask(sample1Active.id)

        //then
        assertThat(api.getTask(sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun completeTask_alreadyCompleted() = runBlockingTest {
        //given
        api.saveTask(sample1Active.id, sample1Active)
        api.completeTask(sample1Active.id)

        //when complete the already completed task
        api.completeTask(sample1Active.id)

        //then no error occurs
        assertThat(api.getTask(sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun activateTask() = runBlockingTest {
        //given
        api.saveTask(sample2Completed.id, sample2Completed)

        //when activate
        api.activateTask(sample2Completed.id)

        //then activated
        assertThat(api.getTask(sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun activateTask_already() = runBlockingTest {
        //given
        api.saveTask(sample2Completed.id, sample2Completed)
        api.activateTask(sample2Completed.id)

        //when activate the already activated task
        api.activateTask(sample2Completed.id)

        //then no error occurs
        assertThat(api.getTask(sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = runBlockingTest {
        //given 2 completed, 1 active
        api.saveTask(sample2Completed.id, sample2Completed)
        api.saveTask(sample2_2Completed.id, sample2_2Completed)
        api.saveTask(sample1Active.id, sample1Active)

        //when clear Completed task
        api.clearCompletedTasks()

        //then size is 1
        assertThat(api.getTasks()).hasSize(1)
    }

    @Test
    fun deleteAllTasks() = runBlockingTest {
        //given 2 task
        api.saveTask(sample2Completed.id, sample2Completed)
        api.saveTask(sample1Active.id, sample1Active)

        //when delete all
        api.deleteAllTasks()

        //then size is 1
        assertThat(api.getTasks()).isEmpty()
    }

    @Test
    fun deleteTask() = runBlockingTest {
        //given 2 task
        api.saveTask(sample1Active.id, sample1Active)
        api.saveTask(sample2Completed.id, sample2Completed)

        //when delete 1
        api.deleteTask(sample1Active.id)

        //then size is 1, only other remains
        val tasks = api.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(sample2Completed.id)
    }
}