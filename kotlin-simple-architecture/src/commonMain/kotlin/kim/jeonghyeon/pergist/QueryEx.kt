package kim.jeonghyeon.pergist

import com.squareup.sqldelight.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T : Any> Query<T>.asListFlow(hasInit: Boolean = true): Flow<List<T>> = getChangeFlow(hasInit) { it.executeAsList() }

inline fun <reified T : Any> Query<T>.asOneFlow(hasInit: Boolean = true): Flow<T> = getChangeFlow(hasInit) { it.executeAsOne() }
inline fun <reified T : Any> Query<T>.asOneOrNullFlow(hasInit: Boolean): Flow<T?> =
    getChangeFlow(hasInit) { it.executeAsOneOrNull() }

/**
 * flow is collected if value is really changed.
 */
inline fun <T : Any, reified U> Query<T>.getChangeFlow(getInit: Boolean = true, crossinline transform: (Query<T>) -> U): Flow<U> {
    val flow: MutableStateFlow<Any?> = MutableStateFlow(if (getInit) transform(this) else INIT)
    addListener(object : Query.Listener {
        override fun queryResultsChanged() {
            flow.value = transform(this@getChangeFlow)
        }
    })

    return flow<U> {
        flow.collect {
            if (it is U) {
                emit(it)
            }
        }
    }
}

object INIT

