package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.Repo
import kim.jeonghyeon.sample.di.serviceLocator

class ApiExternalViewModel(private val api: GithubApi) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.githubApi)

    val repoList = dataFlow<List<Repo>>(listOf())
    val input = dataFlow("kotlin simple architecture")

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