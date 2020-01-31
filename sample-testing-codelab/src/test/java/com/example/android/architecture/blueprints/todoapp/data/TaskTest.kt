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
    fun getTitleForList_titleEmpty() {
        assertThat(sample3TitleEmpty.titleForList).isEqualTo(sample3TitleEmpty.description)
    }

    @Test
    fun getTitleForList_descriptionEmpty() {
        assertThat(sample4DescriptionEmpty.titleForList).isEqualTo(sample4DescriptionEmpty.title)
    }

    @Test
    fun getTitleForList_all() {
        assertThat(sample1Active.titleForList).isEqualTo(sample1Active.title)
    }

    @Test
    fun isActive_active() {
        assertThat(sample1Active.isActive).isTrue()
    }

    @Test
    fun isActive_completed() {
        assertThat(sample2Completed.isActive).isFalse()
    }

    @Test
    fun isEmpty_titleEmpty() {
        Assert.assertTrue(sample3TitleEmpty.isEmpty)
    }

    @Test
    fun isEmpty_descriptionEmpty() {
        Assert.assertTrue(sample4DescriptionEmpty.isEmpty)
    }

    @Test
    fun isEmpty_notEmpty() {
        Assert.assertFalse(sample1Active.isEmpty)
    }
}