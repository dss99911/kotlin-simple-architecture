package kim.jeonghyeon.sample.list.paging

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.paging.Listing
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.sample.list.paging.api.GithubService

class PagingViewModel(val api: GithubService) : BaseViewModel() {
    val keyword = LiveObject<String>()
    val listing = LiveObject<Listing<RepoItemViewModel>>()

    fun onClickSearch() {
        val value = keyword.value
        if (value.isNullOrEmpty()) {
            toast("input keyword")
            return
        }

        listing.value = RepoDataSourceFactory(keyword.value!!, api).asListing.also {
            initState.replaceSource(it.initState)
            state.replaceSource(it.afterState)
        }

    }
}