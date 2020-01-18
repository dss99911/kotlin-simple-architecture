package kim.jeonghyeon.androidlibrary.architecture.mvvm


/**
 * the reason to use Event instead of SingleLiveEvent is that. SingleLiveEvent is class and difficult to integrate with other livedata
 */
@Deprecated("use BaseLiveData")
open class Event<out T>(private val content: T) {

    var handled = false
        private set // Allow external read but not write

    fun handle(): T {
        handled = true
        return content
    }

    /**
     * Returns the content, even if it's already been handled.
     * this is used when one time or multi time both are used.
     */
    fun get(): T = content
}