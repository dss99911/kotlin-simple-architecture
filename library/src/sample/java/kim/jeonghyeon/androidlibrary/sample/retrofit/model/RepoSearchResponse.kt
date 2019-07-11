package kim.jeonghyeon.androidlibrary.sample.retrofit.api

import com.google.gson.annotations.SerializedName
import kim.jeonghyeon.androidlibrary.sample.Repo

data class RepoSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Repo> = emptyList(),
        val nextPage: Int? = null
)