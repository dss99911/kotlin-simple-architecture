//todo change package name
package com.example.sampleandroid

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.view.MainScaffold
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.SignInScreen
import com.example.sampleandroid.view.model.SignUpScreen
import kim.jeonghyeon.androidlibrary.compose.BaseActivity
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.ScreenStack
import kim.jeonghyeon.const.Deeplink.DEEPLINK_PATH_SIGN_IN
import kim.jeonghyeon.const.Deeplink.DEEPLINK_PATH_SIGN_UP
import kotlin.reflect.KClass

class MainActivity : BaseActivity() {
    override val rootScreen: Screen = HomeScreen()

    //todo think about what is the best approach of deeplink
    override val deeplinks: Map<String, Pair<KClass<*>, () -> Screen>> = mapOf(
        DEEPLINK_PATH_SIGN_UP to (SignUpScreen::class to { SignUpScreen() }),
        DEEPLINK_PATH_SIGN_IN to (SignInScreen::class to { SignInScreen() }),
    )
    override val content: @Composable() () -> Unit
        get() = {

            //TODO crash https://youtrack.jetbrains.com/issue/KT-38694
            // java.lang.NoSuchMethodError: No static method setContent$default(Landroidx/activity/ComponentActivity;Landroidx/compose/Recomposer;Lkotlin/jvm/functions/Function0;ILjava/lang/Object;)Landroidx/compose/Composition; in class Landroidx/ui/core/WrapperKt; or its super classes (declaration of 'androidx.ui.core.WrapperKt' appears in /data/app/kim.jeonghyeon.sample.dev-Jwx4yvF_qgle9EcNXesy5Q==/base.apk)
            // if it's fixed. move this code to sample module. instead of sampleandroid module

            AndroidLibraryTheme {
                MainContent()
            }
        }


}

@Composable
fun MainContent() {
    MainScaffold {
        val currentScreen = ScreenStack.last()
        Surface(color = MaterialTheme.colors.background) {
            currentScreen.compose()
        }
        //todo is CrossFade make sub composable to compose several times?

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidLibraryTheme {
        MainContent()
    }
}