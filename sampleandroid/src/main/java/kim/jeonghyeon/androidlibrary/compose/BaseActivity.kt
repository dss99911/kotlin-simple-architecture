package kim.jeonghyeon.androidlibrary.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent

abstract class BaseActivity : AppCompatActivity() {
    //todo onStart, onStop, onResume, on Pause. deliver to current screen

    //TODO crash https://youtrack.jetbrains.com/issue/KT-38694
    // java.lang.NoSuchMethodError: No static method setContent$default(Landroidx/activity/ComponentActivity;Landroidx/compose/Recomposer;Lkotlin/jvm/functions/Function0;ILjava/lang/Object;)Landroidx/compose/Composition; in class Landroidx/ui/core/WrapperKt; or its super classes (declaration of 'androidx.ui.core.WrapperKt' appears in /data/app/kim.jeonghyeon.sample.dev-Jwx4yvF_qgle9EcNXesy5Q==/base.apk)
    // if it's fixed. move this code to sample module. instead of sampleandroid module
    // setContent code should be called here for now
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