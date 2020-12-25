package kim.jeonghyeon.template

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen

@Composable
fun HomeScreen(model: HomeViewModel) {
    Screen(model) {
        Text("Hello Kotlin World.")
    }
}