package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.TaskSamples
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.testing.BaseRobolectricTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

/**
 * Data Access Object for the tasks table.
 */
class TasksRepositoryTest : BaseRobolectricTest() {
    val repo: TaskRepository by inject()

    @Test
    fun getTasks_empty() = runBlockingTest {
        //when empty
        assertThat(repo.getTasks()).isEmpty()
    }

    @Test
    fun getTasks_size1() = runBlockingTest {
        //when size 1
        repo.saveTask(TaskSamples.sample1Active)
        assertThat(repo.getTasks()).hasSize(1)
    }

    @Test
    fun getTasks_size2() = runBlockingTest {
        //when size 2
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)
        assertThat(repo.getTasks()).hasSize(2)
    }

    @Test
    fun getTask_exists() = runBlockingTest {
        //when exists
        repo.saveTask(TaskSamples.sample1Active)
        //no error occurs
        assertThat(repo.getTask(TaskSamples.sample1Active.id)).isEqualTo(TaskSamples.sample1Active)
    }

    @Test
    fun getTask_notExists() = runBlockingTest {
        //when not exists
        val task = repo.getTask(TaskSamples.sample1Active.id)
        //then
        assertThat(task).isNull()
    }

    @Test
    fun saveTask_add1() = runBlockingTest {
        //when add
        repo.saveTask(TaskSamples.sample1Active)

        //then size 1
        assertThat(repo.getTasks()).hasSize(1)
    }

    @Test
    fun saveTask_add2() = runBlockingTest {
        //when add 2 item
        repo.saveTask(TaskSamples.sample2Completed)
        repo.saveTask(TaskSamples.sample3TitleEmpty)

        //then 2 size increased
        assertThat(repo.getTasks()).hasSize(2)
    }

    @Test
    fun saveTask_addSame() = runBlockingTest {
        //when add same task id
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample1Active)

        //then size 1
        assertThat(repo.getTasks()).hasSize(1)
    }

    @Test
    fun completeTask() = runBlockingTest {
        //given
        repo.saveTask(TaskSamples.sample1Active)

        //when complete
        repo.completeTask(TaskSamples.sample1Active.id)

        //then
        assertThat(repo.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun completeTask_already() = runBlockingTest {
        //given
        repo.saveTask(TaskSamples.sample1Active)
        repo.completeTask(TaskSamples.sample1Active.id)

        //when complete the already completed task
        repo.completeTask(TaskSamples.sample1Active.id)

        //then no error occurs
        assertThat(repo.getTask(TaskSamples.sample1Active.id)?.isActive).isFalse()
    }

    @Test
    fun activateTask() = runBlockingTest {
        //given
        repo.saveTask(TaskSamples.sample2Completed)

        //when activate
        repo.activateTask(TaskSamples.sample2Completed.id)

        //then activated
        assertThat(repo.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun activateTask_already() = runBlockingTest {
        //given
        repo.saveTask(TaskSamples.sample2Completed)
        repo.activateTask(TaskSamples.sample2Completed.id)

        //when activate the already activated task
        repo.activateTask(TaskSamples.sample2Completed.id)

        //then no error occurs
        assertThat(repo.getTask(TaskSamples.sample2Completed.id)?.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = runBlockingTest {
        //given 2 completed, 1 active
        repo.saveTask(TaskSamples.sample2Completed)
        repo.saveTask(TaskSamples.sample2_2Completed)
        repo.saveTask(TaskSamples.sample1Active)

        //when clear Completed task
        repo.clearCompletedTasks()

        //then size is 1
        assertThat(repo.getTasks()).hasSize(1)
    }

    @Test
    fun deleteAllTasks() = runBlockingTest {
        //given 2 task
        repo.saveTask(TaskSamples.sample2Completed)
        repo.saveTask(TaskSamples.sample1Active)

        //when delete all
        repo.deleteAllTasks()

        //then size is 1
        assertThat(repo.getTasks()).isEmpty()
    }

    @Test
    fun deleteTask() = runBlockingTest {
        //given 2 task
        repo.saveTask(TaskSamples.sample1Active)
        repo.saveTask(TaskSamples.sample2Completed)

        //when delete 1
        repo.deleteTask(TaskSamples.sample1Active.id)

        //then size is 1, only other remains
        val tasks = repo.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(TaskSamples.sample2Completed.id)
    }
}