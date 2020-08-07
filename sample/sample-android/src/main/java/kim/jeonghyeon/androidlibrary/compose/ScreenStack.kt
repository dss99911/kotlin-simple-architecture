package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.frames.ModelList
import kim.jeonghyeon.extension.removeLast
import org.jetbrains.anko.collections.forEachReversedByIndex

/**
 * !!LIMITATION!!
 * 1. this only support single stack.
 * 2. also, doesn't support multiple activity(how to figure out which activity called a composable function
 * 3. is it possible tree structure? previously, activity contains fragments. so, fragment is changed several times but. after all the process is finished. we can finish activity. simply.
 * todo let's find the way to solve the issue.
 *  search that how to manage view history.
 *  is ambient related to this?
 */
object ScreenStack {

    val instance = ModelList<Screen>()

    inline fun <reified T : Screen> find(): T? = instance.findLast { it is T } as T?

    inline fun <reified T : Screen> findOrNew(defaultScreen: () -> T): T = find()
        ?: (defaultScreen().also { it.push() })

    fun pop(): Screen? =
        if (instance.size == 1) null//root screen can't be popped
        else instance.removeLast()?.also { it.clear() }

    fun last(): Screen = instance.last()

    val size: Int get() = instance.size

    fun push(screen: Screen) {
        instance.add(screen)
    }

    fun replace(screen: Screen) {
        instance.removeLast()?.clear()
        push(screen)
    }

    //todo consider synchronization. and search what is the proper way.
    fun clearAndPush(rootScreen: Screen) {
        instance.forEachReversedByIndex {
            it.clear()
        }
        instance.clear()
        push(rootScreen)
    }

    /**
     * pop until the screen
     * @param inclusive if true, remove the screen also
     * @return if screen not exists in the stack. return false
     */
    fun popUpTo(screen: Screen, inclusive: Boolean): Boolean {
        val screenStack = instance
        val screenIndex = screenStack.indexOf(screen).takeIf { it >= 0 } ?: return false
        val popIndex = screenIndex + (if (inclusive) 0 else 1)


        if (popIndex == screenStack.size) return true//there is nothing to pop

        screenStack.removeAll(screenStack.subList(popIndex, screenStack.size))

        return true
    }
}

fun Screen.push() {
    ScreenStack.push(this)
}

fun Screen.replace() {
    ScreenStack.replace(this)
}

fun Screen.clearAndPush() {
    ScreenStack.clearAndPush(this)
}

fun Screen.popUpTo(inclusive: Boolean): Boolean {
    return ScreenStack.popUpTo(this, inclusive)
}
