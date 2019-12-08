package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.coroutine.loadResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.retrofit.api.GithubService
import kim.jeonghyeon.sample.room.dao.UserDao
import kim.jeonghyeon.sample.room.database.UserDatabase

class ApiCallViewModel(val api: GithubService = GithubService.create(), val dao: UserDao = UserDatabase.instance.userDao()) : BaseViewModel() {
    override fun onCreate() {
        loadResource(state) {
            val searchRepos = api.searchRepos("a", 1, 1)
            api.searchRepos("a", 1, 1)
        }

    }
}