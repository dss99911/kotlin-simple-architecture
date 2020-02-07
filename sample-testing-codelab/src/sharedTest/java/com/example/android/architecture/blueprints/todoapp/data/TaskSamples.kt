package com.example.android.architecture.blueprints.todoapp.data

object TaskSamples {
    val sample1Active
        get() = Task(
            "title1",
            "description",
            false,
            "id1"
        )
    val sample2Completed
        get() = Task(
            "title2",
            "description",
            true,
            "id2"
        )
    val sample2_2Completed
        get() = Task(
            "title2-2",
            "description",
            true,
            "id2_2"
        )
    val sample3TitleEmpty
        get() = Task(
            "",
            "description",
            true,
            "id3"
        )
    val sample4DescriptionEmpty
        get() = Task(
            "title",
            "",
            true,
            "id4"
        )

    val sample5completed
        get() = Task(
            "dsalkfjas;dkflfj",
            "sdsdsd",
            true,
            "id5"
        )

    val sample6completed
        get() = Task(
            "dsalkfjas;112ws1dkflfj",
            "sdssazdsd",
            true,
            "id6"
        )
}