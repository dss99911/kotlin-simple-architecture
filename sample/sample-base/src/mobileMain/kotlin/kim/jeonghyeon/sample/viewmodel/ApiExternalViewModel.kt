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
class ApiExternalViewModel(private val api: GithubApi = serviceLocator.githubApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "External Api call"

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
        repoList.load(status) {
            callApi().map {
                it.name
            }
        }
    }

    private suspend fun callApi() = api.searchRepos(input.value?:"", 1, 10).items
}