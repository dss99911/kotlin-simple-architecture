package com.example.sampleandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.setContent
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import com.example.sampleandroid.common.ScreenStack
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.util.colors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO crash https://youtrack.jetbrains.com/issue/KT-38694
        // java.lang.NoSuchMethodError: No static method setContent$default(Landroidx/activity/ComponentActivity;Landroidx/compose/Recomposer;Lkotlin/jvm/functions/Function0;ILjava/lang/Object;)Landroidx/compose/Composition; in class Landroidx/ui/core/WrapperKt; or its super classes (declaration of 'androidx.ui.core.WrapperKt' appears in /data/app/kim.jeonghyeon.sample.dev-Jwx4yvF_qgle9EcNXesy5Q==/base.apk)
        // if it's fixed. move this code to sample module. instead of sampleandroid module
        setContent {
            AndroidLibraryTheme {
                MainContent()
            }
        }
    }

    //todo move to library
    override fun onBackPressed() {
        ScreenStack.pop() ?: super.onBackPressed()
    }
}

@Composable
fun MainContent() {
    val currentScreen = ScreenStack.screenStack.last()
    Crossfade(currentScreen) {
        Surface(color = colors.background) {
            currentScreen.compose()
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