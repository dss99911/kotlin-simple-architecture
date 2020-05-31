package kim.jeonghyeon.sqldelight

import com.squareup.sqldelight.Query
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveState
import kim.jeonghyeon.androidlibrary.architecture.livedata.loadDataAndState
import kim.jeonghyeon.type.WeakReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

fun <T : Any> Query<T>.asListLiveObject(state: LiveState? = null): LiveObject<List<T>> {
    val liveObject = LiveObject<List<T>>()

    GlobalScope.loadDataAndState(liveObject, state) {
        withContext(Dispatchers.IO) {
            addWeakListener { liveObject.value = executeAsList() }
            executeAsList()
        }
    }

    return liveObject
}

fun Query<*>.addWeakListener(listener: () -> Unit) {
    addListener(WeakListener(listener))
}

class WeakListener(listener: () -> Unit) : Query.Listener {
    val weakReference = WeakReference(listener)
    override fun queryResultsChanged() {
        weakReference.get()?.invoke()
    }
}