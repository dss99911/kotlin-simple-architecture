package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.livedata.plusAssign
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.retrofit.api.GithubService
import kim.jeonghyeon.sample.room.dao.UserDao

class ApiCallViewModel(val api: GithubService, val dao: UserDao) : BaseViewModel() {
    val listing = RepoDataSourceFactory(api).asListing
    init {
        state += listing.loadState
    }

    fun test() {
        state.loadResource {

        }
    }
}