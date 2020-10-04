package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.Repo
import kim.jeonghyeon.sample.di.serviceLocator

class ApiExternalViewModel(private val api: GithubApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.githubApi)

    val repoList by add { DataFlow<List<Repo>>() }
    val input by add { DataFlow("kotlin simple architecture") }

    override fun onInit() {
        repoList.load(initStatus) {
            callApi()
        }
    }

    fun onClickCall() {
        repoList.load(status) {
            callApi()
        }
    }

    private suspend fun callApi() = api.searchRepos(input.value?:"", 1, 10).items
}