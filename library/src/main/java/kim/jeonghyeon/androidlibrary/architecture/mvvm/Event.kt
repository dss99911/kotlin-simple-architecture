package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.livedata.BaseMediatorLiveData

typealias LiveEvent<T> = BaseMediatorLiveData<Event<T>>

fun <T> LiveEvent<T>.call(data: T) {
    postValue(Event(data))
}

fun LiveEvent<Unit>.call() {
    postValue(Event(Unit))
}

fun <T> LiveEvent<T>.observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner, EventObserver(onChanged))
}

/**
 * if event and just data both are required.
 */
fun <T> LiveEvent<T>.observeData(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner) {
        onChanged(it.get())
    }
}

fun <T, U> MediatorLiveData<T>.addEventSource(source: LiveEvent<U>, onChanged: (U) -> Unit) {
    addSource(source, EventObserver(onChanged))
}

fun <T> LiveData<T>.mapEvent() = map { Event(it) }

fun <T> LiveData<T>.distinct() = this.distinctUntilChanged()
/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
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

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>) {
        //Changed by Hyun : if event data is null, previous approach doesn't call callback. so, I checked if it's handled. and then call popContent
        if (!event.handled) {
            onEventUnhandledContent(event.handle())
        }
    }
}