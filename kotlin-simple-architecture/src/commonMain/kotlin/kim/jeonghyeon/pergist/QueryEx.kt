package kim.jeonghyeon.pergist

import com.squareup.sqldelight.Query
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <T : Any> Query<T>.asListFlow(): Flow<List<T>> = asFlow().map { it.executeAsList() }

inline fun <reified T : Any> Query<T>.asOneFlow(hasInit: Boolean = true): Flow<T> = asFlow().map { it.executeAsOne() }
inline fun <reified T : Any> Query<T>.asOneOrNullFlow(hasInit: Boolean): Flow<T?> = asFlow().map { it.executeAsOneOrNull() }

fun <T : Any> Query<T>.asFlow(): Flow<Query<T>> = flow {
    emit(this@asFlow)

    val channel = Channel<Unit>(Channel.CONFLATED)
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            channel.offer(Unit)
        }
    }
    addListener(listener)
    try {
        for (item in channel) {
            emit(this@asFlow)
        }
    } finally {
        removeListener(listener)
    }
}