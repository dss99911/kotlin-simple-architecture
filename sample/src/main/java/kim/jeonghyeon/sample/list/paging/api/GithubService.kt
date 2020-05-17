package kim.jeonghyeon.sample.list.paging.api

import kim.jeonghyeon.androidlibrary.architecture.net.api
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.RepoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
    /**
     * Get repos ordered by stars.
     */
    @GET("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResponse

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            return api(BASE_URL)
        }
    }
}