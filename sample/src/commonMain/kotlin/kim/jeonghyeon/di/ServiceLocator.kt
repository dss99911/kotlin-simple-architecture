package kim.jeonghyeon.di

import kim.jeonghyeon.generated.db.db
import kim.jeonghyeon.net.api
import kim.jeonghyeon.plugin.SimpleConfig
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.type.WeakReference
import kotlin.reflect.KProperty

var serviceLocator: ServiceLocator = ServiceLocatorImpl()

interface ServiceLocator {
    val simpleApi: SimpleApi
    val wordQueries: WordQueries
}

class ServiceLocatorImpl : ServiceLocator {
    //whenever call, make new instance
    override val simpleApi: SimpleApi get() = api("http://${SimpleConfig.BUILD_TIME_LOCAL_IP_ADDRESS}:8080")

    //wordQueries notify to listeners when data is changed.
    //in order that A page change data and B page refresh when data changed, you have to use single instance of Queries.
    //but also if it's not used. need to be cleared.
    override val wordQueries: WordQueries by weak { db<SampleDb>().wordQueries }
}

/**
 * if a service is referred by several pages.
 * use one instance.
 * but if all the pages are cleared. this service also get cleared.
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