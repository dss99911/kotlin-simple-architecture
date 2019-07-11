package kim.jeonghyeon.androidlibrary.sample.retrofit.api

import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.net.api
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
    /**
     * Get repos ordered by stars.
     */
    @GET("search/repositories?sort=stars")
    fun searchRepos(@Query("q") query: String,
                    @Query("page") page: Int,
                    @Query("per_page") itemsPerPage: Int): Call<RepoSearchResponse>

    @GET("search/repositories?sort=stars")
    fun searchRepos2(@Query("q") query: String,
                     @Query("page") page: Int,
                     @Query("per_page") itemsPerPage: Int): LiveData<Resource<RepoSearchResponse>>


    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            return api(BASE_URL)
        }
    }
}