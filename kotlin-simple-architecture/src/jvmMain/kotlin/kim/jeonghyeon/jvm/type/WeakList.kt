package kim.jeonghyeon.jvm.type

import java.lang.ref.WeakReference
import java.util.*

//todo delete?
class WeakList<E> {
    val list = ArrayList<WeakReference<E>>()

    fun addWeakReference(item: E) {
        synchronized(this) {
            list.removeAll { it.get() == null }
            list.add(WeakReference(item))
        }

    }

    fun removeWeakReference(item: E) {
        synchronized(this) {
            list.removeAll { it.get() == null || it.get() == item }
        }
    }

    fun forEachWeakReference(action: (E) -> Unit) {
        list.toTypedArray().forEach { action(it.get() ?: return@forEach) }
    }

    fun contains(item: E) = list.any { it.get() == item }
}