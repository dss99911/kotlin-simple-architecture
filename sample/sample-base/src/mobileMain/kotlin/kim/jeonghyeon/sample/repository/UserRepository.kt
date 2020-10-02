package kim.jeonghyeon.sample.repository

import io.ktor.http.*
import kim.jeonghyeon.auth.OAuthServerName
import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.auth.SignOAuthClient
import kim.jeonghyeon.const.DeeplinkUrl.DEEPLINK_PATH_SIGN_UP
import kim.jeonghyeon.coroutine.retriableResourceFlow
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.removeUserToken
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userDetail: Flow<Resource<SerializableUserDetail>>

    suspend fun signUp(id: String, password: String, name: String)
    suspend fun signGoogle()
    suspend fun signFacebook()
    @Throws(Exception::class)
    fun onOAuthDeeplinkReceived(url: Url)
    suspend fun signIn(id: String, password: String)
    suspend fun signOut()
}

class UserRepositoryImpl(
    private val userApi: UserApi = serviceLocator.userApi,
    private val signApi: SignApi = serviceLocator.signApi,
    private val oauthClient: SignOAuthClient = serviceLocator.oauthClient,
    private val preference: Preference = serviceLocator.preference
) : UserRepository {
    private var retry: ()-> Unit = {}

    //as it's singleton, it keeps data in memory until processor terminated
    override val userDetail: Flow<Resource<SerializableUserDetail>> = retriableResourceFlow {
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

    override suspend fun signGoogle(){
        oauthClient.signUp(OAuthServerName.GOOGLE, DEEPLINK_PATH_SIGN_UP)
    }

    override suspend fun signFacebook() {
        oauthClient.signUp(OAuthServerName.FACEBOOK, DEEPLINK_PATH_SIGN_UP)
    }

    @Throws(Exception::class)
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
        try {
            //even if failed. token should be deleted.
            signApi.signOut()
        } catch (e: Exception) {
            preference.removeUserToken()
        }
        invalidateUser()
    }

    //when userdetail is changed, update it.
    //todo this doesn't suspend until retry is finished. it takes time to refresh after this is invoked. so, sign in page shows different ui while refreshing.
    //todo there is possiblity that server change user data. or client mistakingly doesn't invalidate after user detail changed.
    // so consider to use web socket
    // and also consider use graphQL as this approach need to call api one more time causing user to wait longer time
    private fun invalidateUser() {
        retry()
    }
}