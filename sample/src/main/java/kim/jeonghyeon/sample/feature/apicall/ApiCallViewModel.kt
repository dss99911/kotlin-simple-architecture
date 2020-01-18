package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.livedata.plusAssign
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.retrofit.api.GithubService
import kim.jeonghyeon.sample.room.dao.UserDao
import kim.jeonghyeon.sample.room.database.UserDatabase

class ApiCallViewModel(val api: GithubService = GithubService.create(), val dao: UserDao = UserDatabase.instance.userDao()) : BaseViewModel() {
    val listing = RepoDataSourceFactory(api).asListing
    init {
        state += listing.loadState
    }

    fun test() {
        state.loadResource {

        }
    }
}