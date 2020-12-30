package kim.jeonghyeon.template

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus

@Composable
fun HomeScreen(model: HomeViewModel) {
    Screen(model) {
        Text("Hello Kotlin ${+model.world}")
    }
}