package kim.jeonghyeon.sample.repository

import androidLibrary.sample.samplebase.generated.SimpleConfig
import io.ktor.http.*
import kim.jeonghyeon.auth.OAuthServerName
import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.auth.SignOAuthClient
import kim.jeonghyeon.coroutine.resourceFlow
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userDetail: Flow<Resource<SerializableUserDetail>>

    suspend fun signUp(id: String, password: String, name: String)
    suspend fun signGoogle(deeplinkPath: String)
    suspend fun signFacebook(deeplinkPath: String)
    fun onOAuthDeeplinkReceived(url: Url)
    suspend fun signIn(id: String, password: String)
    suspend fun signOut()
}

class UserRepositoryImpl(
    private val userApi: UserApi = serviceLocator.userApi,
    private val signApi: SignApi = serviceLocator.signApi,
    private val oauthClient: SignOAuthClient = serviceLocator.oauthClient
) : UserRepository {
    private var retry: ()-> Unit = {}

    //as it's singleton, it keeps data in memory until processor terminated
    override val userDetail: Flow<Resource<SerializableUserDetail>> = resourceFlow {
        retry = it
        emit(userApi.getUser())
    }

    override suspend fun signUp(id: String, password: String, name: String) {
        if (id.isBlank()) {
            throw ResourceError("Please input Id")
        }

        if (name.isBlank()) {
            throw ResourceError("Please input Name")
        }

        if (password.isBlank()) {
            throw ResourceError("Please input Password")
        }

        signApi.signUp(
            id,
            password,
            SerializableUserDetail(null, name).toJsonString()
        )
        signApi.signIn(id, password)
        invalidateUser()
    }

    override suspend fun signGoogle(deeplinkPath: String){
        oauthClient.signUp(OAuthServerName.GOOGLE, "${SimpleConfig.serverUrl}$deeplinkPath")
    }

    override suspend fun signFacebook(deeplinkPath: String) {
        oauthClient.signUp(OAuthServerName.FACEBOOK, "${SimpleConfig.serverUrl}$deeplinkPath")
    }

    override fun onOAuthDeeplinkReceived(url: Url) {
        oauthClient.saveToken(url)
        invalidateUser()
    }

    override suspend fun signIn(id: String, password: String) {
        if (id.isBlank()) {
            throw ResourceError("Please input Id")
        }

        if (password.isBlank()) {
            throw ResourceError("Please input Password")
        }

        signApi.signIn(id, password)
        invalidateUser()
    }

    override suspend fun signOut() {
        signApi.signOut()
        invalidateUser()
    }

    //when userdetail is changed, update it.
    //todo there is possiblity that server change user data. or client mistakingly doesn't invalidate after user detail changed.
    // so consider to use web socket
    // and also consider use graphQL as this approach need to call api one more time causing user to wait longer time
    private fun invalidateUser() {
        retry()
    }
}