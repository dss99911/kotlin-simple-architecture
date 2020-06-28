package kim.jeonghyeon.pergist

import com.squareup.sqldelight.Query
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceFlow
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow

fun <T : Any> Query<T>.asListFlow(): ResourceFlow<List<T>> = flow {
    val channel = Channel<Unit>(Channel.CONFLATED).apply { offer(Unit) }

    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            channel.offer(Unit)
        }
    }
    addListener(listener)
    try {
        for (item in channel) {
            val resource = getResource(this@asListFlow) { channel.offer(Unit) }
            resource?.let { emit(it) }
        }
    } finally {
        //todo check if the data refreshed when other Screen update data.
        //this may be called when Screen is not used. then there will be problem.
        //consider to use addWeakListener.
        //solutions
        //1. make ScreenScope which survive when it doesn't exist on history stack.
        //I considered only android. should consider IOS and server as well.
        removeListener(listener)
    }
}

internal fun <T : Any> getResource(query: Query<T>, retry: () -> Unit): Resource<List<T>>? = try {
    Resource.Success(query.executeAsList())
} catch (e: CancellationException) {
    //if cancel. then ignore it
    //todo check if cancel is working
    null
} catch (e: Exception) {
    //todo check if this is working

    Resource.Error(UnknownResourceError(e)) {
        retry()
    }
}
/*

fun Query<*>.addWeakListener(listener: () -> Unit) {
    addListener(WeakListener(listener))
}

class WeakListener(listener: () -> Unit) : Query.Listener {
    val weakReference = WeakReference(listener)
    override fun queryResultsChanged() {
        weakReference.get()?.invoke()
    }
}*/
