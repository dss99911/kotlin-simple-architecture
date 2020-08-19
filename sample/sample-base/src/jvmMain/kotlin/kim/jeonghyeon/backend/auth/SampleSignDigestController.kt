package kim.jeonghyeon.backend.auth

import kim.jeonghyeon.auth.SignBasicController
import kim.jeonghyeon.auth.SignDigestController
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.db.User
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.sample.UserDetailQueries
import kim.jeonghyeon.sample.api.SerializableUserDetail

class SampleSignDigestController(val userDetailQueries: UserDetailQueries = serviceLocator.userDetailQueries) :
    SignDigestController() {

    override suspend fun signUp(id: String,extra: String) {
        val userDetail = extra.fromJsonString<SerializableUserDetail>()
        userDetailQueries.insert(id, userDetail.name)
    }
}
