package com.example.sampleandroid.common

import androidx.compose.frames.ModelList
import com.example.sampleandroid.home.HomeScreen
import kim.jeonghyeon.common.extension.removeLast

interface Screen : Composing

object ScreenStack {
    val screenStack = ModelList<Screen>().apply {
        //todo define main screen on BaseApplication
        add(HomeScreen())
    }

    inline fun <reified T : Screen> find(): T? = screenStack.findLast { it is T } as T?

    inline fun <reified T : Screen> findOrNew(defaultScreen: () -> T): T = find()
        ?: (defaultScreen().also { it.push() })

    fun pop(): Screen? {
        return screenStack.removeLast()
    }
}

fun Screen.push() {
    ScreenStack.screenStack.add(this)
}

//todo consider synchronization. and search what is the proper way.
fun Screen.replace() {
    with(ScreenStack.screenStack) {
        removeLast()
        add(this@replace)
    }
}

fun Screen.clearAndPush() {
    val screenStack = ScreenStack.screenStack
    screenStack.clear()
    screenStack.add(this)

}

/**
 * pop until the screen
 * @param inclusive if true, remove the screen also
 * @return if screen not exists in the stack. return false
 */
fun Screen.popUpTo(inclusive: Boolean): Boolean {
    val screenStack = ScreenStack.screenStack
    val screenIndex = screenStack.indexOf(this).takeIf { it >= 0 } ?: return false
    val popIndex = screenIndex + (if (inclusive) 0 else 1)


    if (popIndex == screenStack.size) return true//there is nothing to pop

    screenStack.removeAll(screenStack.subList(popIndex, screenStack.size))

    return true
}



