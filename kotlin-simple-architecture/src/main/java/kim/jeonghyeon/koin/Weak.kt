package kim.jeonghyeon.koin

import kim.jeonghyeon.type.WeakReference
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

inline fun <reified T : Any> Module.weak(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BeanDefinition<T> {
    return factory(qualifier, override) { param ->
        val key = WeakQualifier(T::class, qualifier)
        (weakMap[key]?.get()
            ?: definition(param).also {
                weakMap.put(key, WeakReference(it))
            }) as T
    }
}


data class WeakQualifier(val kClass: KClass<*>, val qualifier: Qualifier?)

val weakMap = ConcurrentHashMap<WeakQualifier, WeakReference<Any>>()