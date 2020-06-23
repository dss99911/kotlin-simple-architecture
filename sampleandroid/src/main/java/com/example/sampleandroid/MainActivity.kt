package com.example.sampleandroid

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import com.example.sampleandroid.home.HomeScreen
import com.example.sampleandroid.library.BaseActivity
import com.example.sampleandroid.library.Screen
import com.example.sampleandroid.library.ScreenStack
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.util.colors

class MainActivity : BaseActivity() {
    override val rootScreen: Screen = HomeScreen()
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
        Crossfade(currentScreen) {
            Surface(color = colors.background) {
                currentScreen.compose()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidLibraryTheme {
        MainContent()
    }
}