package com.example.android.architecture.blueprints.todoapp.data

object TaskSamples {
    val sample1Active get() = Task("title", "description", false, "id1")
    val sample2Completed get() = Task("title", "description", true, "id2")
    val sample2_2Completed get() = Task("title", "description", true, "id2_2")
    val sample3TitleEmpty get() = Task("", "description", true, "id3")
    val sample4DescriptionEmpty get() = Task("title", "", true, "id4")
}