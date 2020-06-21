package kim.jeonghyeon.delegate

import kim.jeonghyeon.type.WeakReference
import kotlin.reflect.KProperty

/**
 * if a property is referred by several places.
 * use one instance.
 * but if all the reference are cleared. this also get cleared.
 */
class weak<T : Any>(val get: () -> T) {
    var reference: WeakReference<T>? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        var ref = reference?.get()
        if (ref == null) {
            ref = get()
            reference = WeakReference(ref)
        }
        return ref
    }
}