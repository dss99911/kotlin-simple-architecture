package com.example.android.architecture.blueprints.todoapp.data.source.local

import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.testing.BaseRobolectricTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

/**
 * Data Access Object for the tasks table.
 */
class TasksDaoTest : BaseRobolectricTest() {
    val dao: TasksDao by inject()


    @Test
    fun getTasks_empty() = runBlockingTest {
        //when empty
        assertThat(dao.getTasks()).isEmpty()
    }

    @Test
    fun getTasks_size1() = runBlockingTest {
        //when size 1
        dao.saveTask(TaskSamples.sample1Active)
        assertThat(dao.getTasks()).hasSize(1)
    }

    @Test
    fun getTasks_size2() = runBlockingTest {

        //when size 2
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample1Active)
        //then
        assertThat(dao.getTasks()).hasSize(2)
    }

    @Test
    fun getTask_exists() = runBlockingTest {
        //when exists
        dao.saveTask(TaskSamples.sample1Active)
        //no error occurs
        assertThat(dao.getTask(TaskSamples.sample1Active.id)).isEqualTo(TaskSamples.sample1Active)
    }

    @Test
    fun getTask_notExists() = runBlockingTest {
        //when not exists
        val task = dao.getTask(TaskSamples.sample1Active.id)
        //then
        assertThat(task).isNull()
    }


    @Test
    fun saveTask_add1() = runBlockingTest {
        //when add
        dao.saveTask(TaskSamples.sample1Active)

        //then size 1
        assertThat(dao.getTasks()).hasSize(1)
    }

    @Test
    fun saveTask_add2() = runBlockingTest {
        //when add 2 item
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample3TitleEmpty)

        //then 2 size increased
        assertThat(dao.getTasks()).hasSize(2)
    }

    @Test
    fun saveTask_addSame() = runBlockingTest {
        //when add same task id
        dao.saveTask(TaskSamples.sample1Active)
        dao.saveTask(TaskSamples.sample1Active)

        //then size 1
        assertThat(dao.getTasks()).hasSize(1)
    }

    @Test
    fun completeTask() = runBlockingTest {
        //given
        dao.saveTask(TaskSamples.sample1Active)

        //when complete
        dao.completeTask(TaskSamples.sample1Active.id)

        //then
        assertThat(dao.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun completeTask_already() = runBlockingTest {
        //given
        dao.saveTask(TaskSamples.sample1Active)
        dao.completeTask(TaskSamples.sample1Active.id)

        //when complete the already completed task
        dao.completeTask(TaskSamples.sample1Active.id)

        //then no error occurs
        assertThat(dao.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()
    }


    @Test
    fun activateTask() = runBlockingTest {
        //given
        dao.saveTask(TaskSamples.sample2Completed)

        //when activate
        dao.activateTask(TaskSamples.sample2Completed.id)

        //then activated
        assertThat(dao.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun activateTask_already() = runBlockingTest {
        //given
        dao.saveTask(TaskSamples.sample2Completed)
        dao.activateTask(TaskSamples.sample2Completed.id)

        //when activate the already activated task
        dao.activateTask(TaskSamples.sample2Completed.id)

        //then no error occurs
        assertThat(dao.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = runBlockingTest {
        //given 2 completed, 1 active
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample2_2Completed)
        dao.saveTask(TaskSamples.sample1Active)

        //when clear Completed task
        dao.clearCompletedTasks()

        //then size is 1
        assertThat(dao.getTasks()).hasSize(1)
    }

    @Test
    fun deleteAllTasks() = runBlockingTest {
        //given 2 task
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample1Active)

        //when delete all
        dao.deleteAllTasks()

        //then size is 1
        assertThat(dao.getTasks()).isEmpty()
    }

    @Test
    fun deleteTask() = runBlockingTest {
        //given 2 task
        dao.saveTask(TaskSamples.sample1Active)
        dao.saveTask(TaskSamples.sample2Completed)

        //when delete 1
        dao.deleteTask(TaskSamples.sample1Active.id)

        //then size is 1, only other remains
        val tasks = dao.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(TaskSamples.sample2Completed.id)
    }
}