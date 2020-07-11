package com.example.sampleandroid.view.home

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class ViewTabScreen : Screen() {
    override val title: String = R.string.view.resourceToString()

    //todo snack bar, progress bar, edit text, FAB
    // request permission
    // menu on navigation
    // change navigation title
    // start activity, receive result.
    // dialog
    // cacheable image url fetching library like picasso
    // list view. item selection, single selection, multiple selection. click, paging
    // text some part contains link and other text style.
    // somehow supporting navigation. there should be some way to see Screen flow easily.
    // splash page(add logic and branch different screen)
    // mediator screen which contains history stack(like relationship with activity and fragment)
    // Screen extends frequently used functions or modifier or theme. so that easily use. it takes time to import functions or properties.
    // block back button when modal dialog or progress bar is shown.
    // routing function on Screen. so, provide history sub stack for each screen. if screen is not shown then state will be cleared. so. need to keep on Screen.
    // deeplink
    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Text(title)
    }
}

