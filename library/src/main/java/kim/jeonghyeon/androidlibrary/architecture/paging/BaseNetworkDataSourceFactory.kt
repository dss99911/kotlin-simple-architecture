package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.toLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.architecture.livedata.isLoading
import kim.jeonghyeon.androidlibrary.architecture.livedata.observeOneTime

abstract class BaseNetworkDataSourceFactory<ITEM, RDATA>(private val pageSize: Int) : DataSource.Factory<Int, ITEM>() {

    private val sourceLiveData = MutableLiveData<BaseNetworkDataSource<ITEM, RDATA>>()

    abstract fun createCall(page: Int, pageSize: Int): LiveData<Resource<RDATA>>

    abstract fun getListFromResponseData(data: RDATA?): List<ITEM>?


    override fun create(): DataSource<Int, ITEM> = object : BaseNetworkDataSource<ITEM, RDATA>() {
        override fun createCall(page: Int, pageSize: Int) =
                this@BaseNetworkDataSourceFactory.createCall(page, pageSize)

        override fun getListFromResponseData(data: RDATA?): List<ITEM>? =
                this@BaseNetworkDataSourceFactory.getListFromResponseData(data)


    }.also { sourceLiveData.postValue(it) }

    val asListing: Listing<ITEM>
        get() {
            val livePagedList = toLiveData(pageSize)
            return Listing(
                    data = livePagedList,
                    loadState = Transformations.switchMap(sourceLiveData) { it.loadState },
                    retry = { sourceLiveData.value?.retryFailed() },
                    refresh = { sourceLiveData.value?.invalidate() }
            )
        }
}

private abstract class BaseNetworkDataSource<ITEM, RDATA> : PageKeyedDataSource<Int, ITEM>() {

    val loadState = MutableLiveData<ResourceState>()
    var lastRequestedPage = 1

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    fun retryFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    abstract fun createCall(page: Int, pageSize: Int): LiveData<Resource<RDATA>>

    abstract fun getListFromResponseData(data: RDATA?): List<ITEM>?

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ITEM>) {
        loadState.postValue(ResourceState.LOADING)

        lastRequestedPage = 1
        val page = lastRequestedPage

        val response = createCall(page, params.requestedLoadSize)
        response.observeOneTime {
            if (it.isLoading) {
                return@observeOneTime false
            }

            when (it) {
                is Resource.Success -> {
                    loadState.postValue(ResourceState.SUCCESS)
                    callback.onResult(getListFromResponseData(it.data)
                            ?: emptyList(), page - 1, page + 1)
                }

                is Resource.Error -> {
                    retry = {
                        loadInitial(params, callback)
                    }

                    loadState.postValue(
                            ResourceState.error(it.error)
                    )
                }
                else -> {
                    //loading state doesn't reach here
                }
            }
            return@observeOneTime true
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ITEM>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ITEM>) {
        loadState.postValue(ResourceState.LOADING)
        val page = ++lastRequestedPage

        val response = createCall(page, params.requestedLoadSize)
        response.observeOneTime {
            if (it.isLoading) {
                return@observeOneTime false
            }

            when (it) {
                is Resource.Success -> {
                    loadState.postValue(ResourceState.SUCCESS)
                    callback.onResult(getListFromResponseData(it.data) ?: emptyList(), page + 1)
                }
                is Resource.Error -> {
                    retry = {
                        loadAfter(params, callback)
                    }

                    loadState.postValue(
                            ResourceState.error(it.error)
                    )
                }
                else -> {
                    //loading state doesn't reach here
                }
            }
            return@observeOneTime true
        }
    }
}