package kim.jeonghyeon.androidlibrary.compose.screen

import androidx.ui.graphics.vector.VectorAsset
import kim.jeonghyeon.androidlibrary.compose.Screen

abstract class TabView : Screen() {
    open val icon: VectorAsset? = null

}