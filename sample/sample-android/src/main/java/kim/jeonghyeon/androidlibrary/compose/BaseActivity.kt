package kim.jeonghyeon.androidlibrary.compose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import io.ktor.http.*
import kotlin.reflect.KClass

abstract class BaseActivity : AppCompatActivity() {
    //todo onStart, onStop, onResume, on Pause. deliver to current screen

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
     */
    open val deeplinks: Map<String, Pair<KClass<*>, () -> Screen>> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            rootScreen.push()
            content()
        }

        deliverDeeplink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        deliverDeeplink(intent)
    }

    private fun deliverDeeplink(intent: Intent?) {
        val screen = deeplinks[intent?.data?.path?:return]?:return
        //single top
        var last = ScreenStack.last()
        if (last::class != screen.first) {
            last = screen.second().apply { push() }
        }

        last.onDeeplinkReceived(Url(intent?.dataString!!))
    }

    override fun onBackPressed() {
        ScreenStack.pop() ?: super.onBackPressed()
    }
}