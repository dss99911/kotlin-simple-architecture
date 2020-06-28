package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewModule
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.screen.TabView
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class ViewTabView : TabView() {
    override val icon: VectorAsset? = Icons.Filled.ViewModule
    override val title: String = R.string.view.resourceToString()

    //todo snack bar, progress bar, edit text, FAB
    // request permission
    // menu
    // start activity, receive result.
    // dialog
    // cacheable image url fetching library like picasso
    // list view. item selection, single selection, multiple selection. click, paging

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Text(title)
    }
}

