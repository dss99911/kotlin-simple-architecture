package kim.jeonghyeon.template

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.base.helloText
import kim.jeonghyeon.base.HomeViewModel

@Composable
fun HomeScreen(model: HomeViewModel) {
    Screen(model) {
        Text("$helloText ${+model.world}")
    }
}