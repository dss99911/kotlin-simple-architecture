package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Get
import kim.jeonghyeon.annotation.Query
import kotlinx.serialization.Serializable

@Api("https://api.github.com")
interface GithubApi {
    @Get("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResponse
}

@Serializable
data class RepoSearchResponse(
    val total_count: Int,
    val incomplete_results: Boolean,//todo if there is more result, it should be true. but this always false.
    val items: List<Repo> = emptyList()
)

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    val html_url: String?,
    val stargazers_count: Int?,
    val forks_count: Int?,
    val language: String?
)
