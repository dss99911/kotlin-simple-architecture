package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.TodoKoinTest
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample1Active
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample2Completed
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample2_2Completed
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample3TitleEmpty
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class TaskApiTest : TodoKoinTest() {
    val api: TaskApi by inject()

    @Before
    fun before() = runBlocking {
        api.deleteAllTasks()
    }

    @Test
    fun getTasks() = runBlocking {
        //when empty
        assertThat(api.getTasks()).isEmpty()

        //when size 1
        api.saveTask(sample1Active.id, sample1Active)
        assertThat(api.getTasks()).hasSize(1)

        //when size 2
        api.saveTask(sample1Active.id, sample2Completed)
        assertThat(api.getTasks()).hasSize(2)
    }

    @Test
    fun getTask() = runBlocking {
        //when not exists
        val task = api.getTask(sample1Active.id)
        //Then null
        assertThat(task).isNull()

        //when exists
        api.saveTask(sample1Active.id, sample1Active)
        //then no error occurs
        assertThat(api.getTask(sample1Active.id)).isEqualTo(sample1Active)
    }

    @Test
    fun saveTask() = runBlocking {
        //when add
        api.saveTask(sample1Active.id, sample1Active)

        //then size 1
        assertThat(api.getTasks()).hasSize(1)

        //when add same task id
        api.saveTask(sample1Active.id, sample1Active)

        //then size 1
        assertThat(api.getTasks()).hasSize(1)

        //when add 2 item
        api.saveTask(sample2Completed.id, sample2Completed)
        api.saveTask(sample3TitleEmpty.id, sample3TitleEmpty)

        //then 2 size increased
        assertThat(api.getTasks()).hasSize(3)
    }

    @Test
    fun completeTask() = runBlocking {
        //given
        api.saveTask(sample1Active.id, sample1Active)

        //when complete
        api.completeTask(sample1Active.id)

        //then
        assertThat(api.getTask(sample1Active.id)?.isActive).isFalse()

        //when complete the already completed task
        api.completeTask(sample1Active.id)

        //then no error occurs
        assertThat(api.getTask(sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun activateTask() = runBlocking {
        //given
        api.saveTask(sample2Completed.id, sample2Completed)

        //when activate
        api.activateTask(sample2Completed.id)

        //then activated
        assertThat(api.getTask(sample2Completed.id)?.isActive).isTrue()

        //when activate the already activated task
        api.activateTask(sample2Completed.id)

        //then no error occurs
        assertThat(api.getTask(sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = runBlocking {
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
    fun deleteAllTasks() = runBlocking {
        //given 2 task
        api.saveTask(sample2Completed.id, sample2Completed)
        api.saveTask(sample1Active.id, sample1Active)

        //when delete all
        api.deleteAllTasks()

        //then size is 1
        assertThat(api.getTasks()).isEmpty()
    }

    @Test
    fun deleteTask() = runBlocking {
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