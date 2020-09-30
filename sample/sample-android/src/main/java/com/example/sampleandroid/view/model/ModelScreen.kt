package com.example.sampleandroid.view.model

import com.example.sampleandroid.view.SubScreen
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

abstract class ModelScreen(viewModel: SampleViewModel = SampleViewModel()) : SubScreen(viewModel) {
    override val parentTitle: String = R.string.model.resourceToString()

    companion object {
        //this is just sample. creating Screen instance two times is not good in memory(though it's little)
        //but, in order to show title easily, created two times
        //Don't follow this on real project.
        val screens = listOf(
            ApiSingleScreen().title to { ApiSingleScreen() },
            ApiSequentialScreen().title to { ApiSequentialScreen() },
            ApiParallelScreen().title to { ApiParallelScreen() },
            ApiPollingScreen().title to { ApiPollingScreen() },
            DbSimpleScreen().title to { DbSimpleScreen() },
            ApiDbScreen().title to { ApiDbScreen() },
            ApiHeaderScreen().title to { ApiHeaderScreen() },
            ApiAnnotationScreen().title to { ApiAnnotationScreen() },
            ApiExternalScreen().title to { ApiExternalScreen() },
            UserScreen().title to { UserScreen() },
            ApiBindingScreen().title to { ApiBindingScreen() },
        )
    }
}