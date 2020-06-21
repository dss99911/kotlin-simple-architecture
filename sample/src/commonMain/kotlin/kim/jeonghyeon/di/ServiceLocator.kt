package kim.jeonghyeon.di

import kim.jeonghyeon.delegate.weak
import kim.jeonghyeon.generated.db.db
import kim.jeonghyeon.net.api
import kim.jeonghyeon.plugin.SimpleConfig
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi

var serviceLocator: ServiceLocator = ServiceLocatorImpl()

val baseUrl: String = "http://${SimpleConfig.BUILD_TIME_LOCAL_IP_ADDRESS}:8080"

interface ServiceLocator {
    val simpleApi: SimpleApi
    val wordQueries: WordQueries
}

class ServiceLocatorImpl : ServiceLocator {
    //whenever call, make new instance
    override val simpleApi: SimpleApi get() = api(baseUrl)

    //wordQueries notify to listeners when data is changed.
    //in order that A page change data and B page refresh when data changed, you have to use single instance of Queries.
    //but also if it's not used. need to be cleared.
    override val wordQueries: WordQueries by weak { db<SampleDb>().wordQueries }
}