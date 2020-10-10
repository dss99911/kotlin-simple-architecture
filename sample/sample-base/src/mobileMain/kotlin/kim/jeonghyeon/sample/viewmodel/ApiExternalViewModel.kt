package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.Repo
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log

/**
 * shows how to call external api
 * similar with [ApiAnnotationViewModel]
 */
class ApiExternalViewModel(private val api: GithubApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.githubApi)

    val repoList by add { DataFlow<List<String>>() }
    val input by add { DataFlow("kotlin simple architecture") }

    override fun onInit() {
        repoList.load(initStatus) {
            callApi().map {
                it.name
            }
        }
    }

    fun onClickCall() {
        log.i("onClickCall ${status.value}")
        repoList.load(status) {
            log.i("callApi before")
            callApi().map {
                it.name
            }.also {
                log.i("callApi")
            }

        }
    }

    private suspend fun callApi() = api.searchRepos(input.value?:"", 1, 10).items
}