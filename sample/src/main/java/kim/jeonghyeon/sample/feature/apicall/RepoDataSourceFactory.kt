package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.paging.BaseNetworkDataSourceFactory
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.RepoSearchResponse
import kim.jeonghyeon.sample.retrofit.api.GithubService

class RepoDataSourceFactory(val api: GithubService = GithubService.create()) : BaseNetworkDataSourceFactory<RepoItemViewModel, RepoSearchResponse>(SIZE) {
    companion object {
        const val SIZE = 10
    }

    override suspend fun createCall(page: Int, pageSize: Int): RepoSearchResponse =
        api.searchRepos("kotlin", page, pageSize)

    override fun getNextPageFromResponseData(
        data: RepoSearchResponse,
        currentPage: Int,
        currentRequestedPageSize: Int
    ): Int? = if (data.incompleteResults) SIZE / currentRequestedPageSize + currentPage else null

    override fun getListFromResponseData(data: RepoSearchResponse): List<RepoItemViewModel> =
        data.items.map {
            RepoItemViewModel(it)
        }
}