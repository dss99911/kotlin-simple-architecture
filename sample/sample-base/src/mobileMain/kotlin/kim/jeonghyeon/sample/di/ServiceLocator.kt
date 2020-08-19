package kim.jeonghyeon.sample.di

import androidLibrary.sample.samplebase.generated.db.db
import androidLibrary.sample.samplebase.generated.net.create
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.auth.createSignApi
import kim.jeonghyeon.delegate.weak
import kim.jeonghyeon.net.*
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.*
import kim.jeonghyeon.sample.repository.WordRepository
import kim.jeonghyeon.sample.repository.WordRepositoryImpl

var serviceLocator: ServiceLocator = ServiceLocatorImpl()

interface ServiceLocator {
    val preferenceApi: PreferenceApi
    val preference: Preference
    val sampleApi: SampleApi
    val githubApi: GithubApi
    val signApi: SignApi
    val userApi: UserApi
    val wordQueries: WordQueries
    val wordRepository: WordRepository
}

class ServiceLocatorImpl : ServiceLocator {
    //whenever call, make new instance
    override val sampleApi: SampleApi get() = api()
    override val githubApi: GithubApi get() = api()
    override val signApi: SignApi get() = client.createSignApi(serverUrl, AUTH_TYPE)
    override val userApi: UserApi get() = api()
    override val preferenceApi: PreferenceApi get() = apiSimple()
    override val preference: Preference = Preference()

    //wordQueries notify to listeners when data is changed.
    //in order that A page change data and B page refresh when data changed, you have to use single instance of Queries.
    //but also if it's not used. need to be cleared.
    override val wordQueries: WordQueries by weak { db<SampleDb>().wordQueries }
    override val wordRepository: WordRepository get() = WordRepositoryImpl(sampleApi, wordQueries)
}