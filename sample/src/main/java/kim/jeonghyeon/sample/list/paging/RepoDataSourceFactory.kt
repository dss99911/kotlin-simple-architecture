package kim.jeonghyeon.sample.list.paging

import kim.jeonghyeon.androidlibrary.architecture.paging.BaseNetworkDataSourceFactory
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.RepoSearchResponse
import kim.jeonghyeon.sample.list.paging.api.GithubService

class RepoDataSourceFactory(val keyword: String, val api: GithubService) :
    BaseNetworkDataSourceFactory<RepoItemViewModel, RepoSearchResponse>(
        SIZE
    ) {
    companion object {
        const val SIZE = 10
    }

    override suspend fun createCall(page: Int, pageSize: Int): RepoSearchResponse =
        api.searchRepos(keyword, page, pageSize)

    override fun getNextPageFromResponseData(
        data: RepoSearchResponse,
        currentPage: Int,
        currentRequestedPageSize: Int
    ): Int? = if (data.incompleteResults) currentRequestedPageSize / SIZE + currentPage else null

    override fun getListFromResponseData(data: RepoSearchResponse/* todo hyun , requestPage: Int*/): List<RepoItemViewModel> =
        data.items.map {
            RepoItemViewModel(it)
        }
}