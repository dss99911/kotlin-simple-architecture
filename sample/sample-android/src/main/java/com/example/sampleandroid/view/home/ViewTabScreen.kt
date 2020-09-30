package com.example.sampleandroid.view.home

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sampleandroid.view.SampleScreen
import com.example.sampleandroid.view.view.ViewScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R

class ViewTabScreen : SampleScreen() {
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
    // consider navigation link like swiftui for stack. so, navigation stack is saved on the navigation view
    // savedstate
    // view stack은 ios의 NavigationView 같은 방식으로 하는게 좋을듯..
    // viewpager, list view(simple, checked, onClick, paging)
    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        ScrollableColumn {
            ViewScreen.screens.forEach {
                Button(
                    onClick = { it.second().push() },
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(it.first)
                }
            }
        }
    }
}

