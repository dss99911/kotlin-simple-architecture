package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.compose.frames.ModelList
import androidx.compose.remember
import androidx.ui.foundation.Text
import androidx.ui.layout.Stack
import androidx.ui.material.TextButton
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.util.log
import kim.jeonghyeon.common.extension.removeLast
import kotlinx.coroutines.CoroutineScope

abstract class Screen : Composing {
    open val title: String = ""

    /**
     * similar with [status] but full page error is shown.
     */
    val initStatus = statusStateOf()

    /**
     * screen status of error or loading for event like click
     */
    val status = statusStateOf()

    private lateinit var scope: CoroutineScope

    @Composable
    protected open fun view() {

    }

    protected open fun initialize() {

    }

    @Composable
    fun compose() {
        scope = composableCoroutineScope()

        remember {
            initialize()
        }

        Stack {
            view()

            Stack {
                statusHandle()
            }
        }
    }

    @Composable
    fun statusHandle() {
        when (status.value) {
            is Resource.Loading -> composeLoading()
            is Resource.Error -> composeError()
            else -> {
            }
        }

        when (initStatus.value) {
            is Resource.Loading -> composeFullLoading()
            is Resource.Error -> composeFullError()
        }
    }

    fun <T> ResourceState<T>.load(work: suspend CoroutineScope.() -> T): ResourceState<T> {
        scope.loadResource(this, work)
        return this
    }

    @Composable
    fun <T> ResourceState<T>.loadInComposition(work: suspend CoroutineScope.() -> T): ResourceState<T> {
        remember {
            scope.loadResource(this, work)
        }

        return this
    }

    fun <T> ResourceState<T>.load(status: StatusState, work: suspend CoroutineScope.() -> T): ResourceState<T> {
        scope.loadResource(this, status, work)
        return this
    }

    @Composable
    fun <T> ResourceState<T>.loadInComposition(status: StatusState, work: suspend CoroutineScope.() -> T): ResourceState<T> {
        remember {
            log("load in composition " + this@Screen::class.java.name)
            scope.loadResource(this, status, work)

        }
        return this
    }


    @Composable
    protected fun composeLoading() {
        Text("Loadding...")
    }

    @Composable
    protected fun composeFullLoading() {
        Text("Loadding...")
    }

    @Composable
    protected fun composeError() {
        TextButton(onClick = { status.value.onError { it.retry() } }) {
            Text("error")
        }
    }

    @Composable
    protected fun composeFullError() {
        TextButton(onClick = { status.value.onError { it.retry() } }) {
            Text("full error")
        }
    }
}

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
        else instance.removeLast()

    fun last(): Screen = instance.last()

    val size: Int get() = instance.size

    fun push(screen: Screen) {
        instance.add(screen)
    }

    fun replace(screen: Screen) {
        instance.removeLast()
        push(screen)
    }

    //todo consider synchronization. and search what is the proper way.
    fun clearAndPush(rootScreen: Screen) {
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

