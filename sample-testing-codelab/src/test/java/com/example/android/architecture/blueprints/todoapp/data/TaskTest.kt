package com.example.android.architecture.blueprints.todoapp.data

import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample1Active
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample2Completed
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample3TitleEmpty
import com.example.android.architecture.blueprints.todoapp.data.TaskSamples.sample4DescriptionEmpty
import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test

class TaskTest {
    @Test
    fun getTitleForList() {
        assertThat(sample3TitleEmpty.titleForList).isEqualTo(sample3TitleEmpty.description)

        assertThat(sample4DescriptionEmpty.titleForList).isEqualTo(sample4DescriptionEmpty.title)

        assertThat(sample1Active.titleForList).isEqualTo(sample1Active.title)
    }

    @Test
    fun isActive() {
        assertThat(sample1Active.isActive).isTrue()
        assertThat(sample2Completed.isActive).isFalse()
    }

    @Test
    fun isEmpty() {
        Assert.assertTrue(sample3TitleEmpty.isEmpty)
        Assert.assertTrue(sample4DescriptionEmpty.isEmpty)
        Assert.assertFalse(sample1Active.isEmpty)
    }
}