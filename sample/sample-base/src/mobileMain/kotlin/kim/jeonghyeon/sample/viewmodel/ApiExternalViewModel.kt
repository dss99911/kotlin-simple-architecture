package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.value
import kim.jeonghyeon.client.valueOrNull
import kim.jeonghyeon.sample.api.GithubApi
import kim.jeonghyeon.sample.api.Repo
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * shows how to call external api
 * similar with [ApiAnnotationViewModel]
 */
class ApiExternalViewModel(private val api: GithubApi = serviceLocator.githubApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "External Api call"

    val repoList by add { flowViewModel<List<String>>() }
    val input by add { flowViewModel("kotlin simple architecture") }

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

    private suspend fun callApi() = api.searchRepos(input.valueOrNull?:"", 1, 10).items
}

class ApiExternalViewModel2(private val api: GithubApi = serviceLocator.githubApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "External Api call"


    val input by add { flowViewModel("kotlin simple architecture") }
    val clickCall = flowViewModel<Unit>()

    val repoList by add {
        merge(
            initFlow.map {
                callApi().map { it.name }
            },
            clickCall.mapInIdle {
                callApi().map { it.name }
            }
        )

    }

    private suspend fun callApi() = api.searchRepos(input.valueOrNull?:"", 1, 10).items
}