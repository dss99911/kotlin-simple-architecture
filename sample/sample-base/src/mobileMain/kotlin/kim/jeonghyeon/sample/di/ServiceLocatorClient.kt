package kim.jeonghyeon.sample.di

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.auth.SignOAuthClient
import kim.jeonghyeon.auth.createSignApi
import kim.jeonghyeon.net.AUTH_TYPE_SIGN_IN
import kim.jeonghyeon.net.api
import kim.jeonghyeon.net.client
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.sample.repository.UserRepositoryImpl
import kim.jeonghyeon.sample.repository.WordRepository
import kim.jeonghyeon.sample.repository.WordRepositoryImpl
import kim.jeonghyeon.type.weak
import samplebase.generated.SimpleConfig
import samplebase.generated.db.db

var serviceLocator: ServiceLocatorClient = ServiceLocatorClientImpl()

interface ServiceLocatorClient {
    val preferenceApi: PreferenceApi
    val preference: Preference
    val sampleApi: SampleApi
    val githubApi: GithubApi
    val signApi: SignApi
    val oauthClient: SignOAuthClient
    val userApi: UserApi
    val userRepository: UserRepository
    val wordQueries: WordQueries
    val wordRepository: WordRepository
}

/**
 * 1. testable by injecting mock service
 * 2. caller doesn't take care of lifecycle of each service (singleton, factory, weak, etc)
 */
class ServiceLocatorClientImpl : ServiceLocatorClient {
    //whenever call, make new instance
    override val sampleApi: SampleApi get() = api()
    override val githubApi: GithubApi get() = api()
    override val signApi: SignApi get() = client.createSignApi(SimpleConfig.serverUrl, AUTH_TYPE_SIGN_IN)
    override val oauthClient: SignOAuthClient get() = SignOAuthClient(SimpleConfig.serverUrl)
    override val userApi: UserApi get() = api()
    override val userRepository: UserRepository by lazy { UserRepositoryImpl() }
    override val preferenceApi: PreferenceApi get() = api()
    override val preference: Preference = Preference()

    //wordQueries notify to listeners when data is changed.
    //in order that A page change data and B page refresh when data changed, you have to use single instance of Queries.
    //but also if it's not used. need to be cleared.
    override val wordQueries: WordQueries by weak { db<SampleDb>().wordQueries }
    override val wordRepository: WordRepository get() = WordRepositoryImpl(sampleApi, wordQueries)
}