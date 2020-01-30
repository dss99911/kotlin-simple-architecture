package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.database.sqlite.SQLiteConstraintException
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.example.android.architecture.blueprints.todoapp.util.BaseRobolectricTest
import com.example.android.architecture.blueprints.todoapp.util.assertNotReachable
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.inject

/**
 * Data Access Object for the tasks table.
 */
class TasksDaoTest : BaseRobolectricTest() {
    val dao: TasksDao by inject()

    @Test
    fun getTasks() = runBlocking {
        //when empty
        assertThat(dao.getTasks()).isEmpty()

        //when size 1
        dao.saveTask(TaskSamples.sample1Active)
        assertThat(dao.getTasks()).hasSize(1)

        //when size 2
        dao.saveTask(TaskSamples.sample2Completed)
        assertThat(dao.getTasks()).hasSize(2)
    }

    @Test
    fun getTask() = runBlocking {
        //when not exists
        val task = dao.getTask(TaskSamples.sample1Active.id)
        assertThat(task).isNull()

        //when exists
        dao.saveTask(TaskSamples.sample1Active)
        //no error occurs
        assertThat(dao.getTask(TaskSamples.sample1Active.id)).isEqualTo(TaskSamples.sample1Active)
    }

    @Test
    fun saveTask() = runBlocking {
        //when add
        dao.saveTask(TaskSamples.sample1Active)

        //then size 1
        assertThat(dao.getTasks()).hasSize(1)

        //when add same task id
        try {
            dao.saveTask(TaskSamples.sample1Active)
            assertNotReachable()
        } catch (e: SQLiteConstraintException) {
            //then error occurs
            assertThat(dao.getTasks()).hasSize(1)
        }

        //when add 2 item
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample3TitleEmpty)

        //then 2 size increased
        assertThat(dao.getTasks()).hasSize(3)
    }

    @Test
    fun completeTask() = runBlocking {
        //given
        dao.saveTask(TaskSamples.sample1Active)

        //when complete
        dao.completeTask(TaskSamples.sample1Active.id)

        //then
        assertThat(dao.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()

        //when complete the already completed task
        dao.completeTask(TaskSamples.sample1Active.id)

        //then no error occurs
        assertThat(dao.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun activateTask() = runBlocking {
        //given
        dao.saveTask(TaskSamples.sample2Completed)

        //when activate
        dao.activateTask(TaskSamples.sample2Completed.id)

        //then activated
        assertThat(dao.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()

        //when activate the already activated task
        dao.activateTask(TaskSamples.sample2Completed.id)

        //then no error occurs
        assertThat(dao.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = runBlocking {
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
    fun deleteAllTasks() = runBlocking {
        //given 2 task
        dao.saveTask(TaskSamples.sample2Completed)
        dao.saveTask(TaskSamples.sample1Active)

        //when delete all
        dao.deleteAllTasks()

        //then size is 1
        assertThat(dao.getTasks()).isEmpty()
    }

    @Test
    fun deleteTask() = runBlocking {
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