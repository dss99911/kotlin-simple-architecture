package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.Repo
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiExternalViewModel(private val api: GithubApi = serviceLocator.githubApi) : BaseViewModel() {
    val repoList = MutableStateFlow<List<Repo>>(listOf())
    val input = MutableStateFlow("kotlin simple architecture")

    override fun onInitialized() {
        repoList.load(initStatus) {
            callApi()
        }
    }

    fun onClickCall() {
        repoList.load(status) {
            callApi()
        }
    }

    private suspend fun callApi() = api.searchRepos(input.value, 1, 10).items
}