package kim.jeonghyeon.androidlibrary.compose

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.util.nativeClass
import io.ktor.http.*
import kotlin.reflect.KClass

abstract class BaseActivity : AppCompatActivity() {
    //TODO crash https://youtrack.jetbrains.com/issue/KT-38694
    // java.lang.NoSuchMethodError: No static method setContent$default(Landroidx/activity/ComponentActivity;Landroidx/compose/Recomposer;Lkotlin/jvm/functions/Function0;ILjava/lang/Object;)Landroidx/compose/Composition; in class Landroidx/ui/core/WrapperKt; or its super classes (declaration of 'androidx.ui.core.WrapperKt' appears in /data/app/kim.jeonghyeon.sample.dev-Jwx4yvF_qgle9EcNXesy5Q==/base.apk)
    // if it's fixed. move this code to sample module. instead of sampleandroid module
    // setContent code should be called here for now
    abstract val content: @Composable() () -> Unit

    abstract val rootScreen: Screen

    /**
     * Activity's launch mode is singleInstance : one task, one activity only
     * Screen's launch mode is singleTop : if the screen is on top, just send 'onDeeplinkReceived' event
     * todo consider navigate screens by deeplink only.
     * todo consider to delete KClass<*>
     */
    open val deeplinks: Map<String, Pair<KClass<*>, () -> Screen>> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //when I test with Jetsurvey sample code
        //snackbar is shown above keyboard without setting this.
        //but, it's not working in this project, so, added here, as I think this should be default behavior
        //If there is way it's working without this, this can be removable
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        Deeplinker.deeplinks = deeplinks

        setContent {
            rootScreen.push()
            content()
        }

        intent?.dataString?.let {
            Deeplinker.navigateToDeeplink(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.dataString?.let {
            Deeplinker.navigateToDeeplink(it)
        }
    }

    override fun onBackPressed() {
        ScreenStack.pop() ?: super.onBackPressed()
    }
}