package kim.jeonghyeon.androidlibrary.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent

abstract class BaseActivity : AppCompatActivity() {
    //todo onStart, onStop, onResume, on Pause. deliver to current screen
    abstract val content: @Composable() () -> Unit

    abstract val rootScreen: Screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            rootScreen.push()
            content()
        }
    }

    override fun onBackPressed() {
        ScreenStack.pop() ?: super.onBackPressed()
    }
}