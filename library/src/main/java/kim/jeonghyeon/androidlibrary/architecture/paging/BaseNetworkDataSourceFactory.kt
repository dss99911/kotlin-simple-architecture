package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.toLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.*

abstract class BaseNetworkDataSourceFactory<ITEM, RDATA : Any>(private val pageSize: Int) : DataSource.Factory<String, ITEM>() {

    private val sourceLiveData = MutableLiveData<BaseNetworkDataSource<ITEM, RDATA>>()

    abstract fun createCall(page: String?, pageSize: Int): LiveData<Resource<RDATA>>

    abstract fun getListFromResponseData(data: RDATA?): List<ITEM>?
    abstract fun getNextPageFromResponseData(data: RDATA?): String?


    override fun create(): DataSource<String, ITEM> = object : BaseNetworkDataSource<ITEM, RDATA>() {

        override fun createCall(page: String?, pageSize: Int): LiveData<Resource<RDATA>> =
                this@BaseNetworkDataSourceFactory.createCall(page, pageSize)

        override fun getListFromResponseData(data: RDATA?): List<ITEM>? =
                this@BaseNetworkDataSourceFactory.getListFromResponseData(data)

        override fun getNextPageFromResponseData(data: RDATA?) = this@BaseNetworkDataSourceFactory.getNextPageFromResponseData(data)
    }.also { sourceLiveData.postValue(it) }

    val asListing: Listing<ITEM>
        get() {
            val livePagedList = toLiveData(pageSize)
            return Listing(
                    data = livePagedList,
                    loadState = sourceLiveData.switchMap { it.loadState },
                    retry = { sourceLiveData.value?.retryFailed() },
                    refresh = { sourceLiveData.value?.invalidate() }
            )
        }
}

private abstract class BaseNetworkDataSource<ITEM, RDATA : Any> : PageKeyedDataSource<String, ITEM>() {

    val loadState = MutableLiveData<ResourceState>()

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    fun retryFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    /**
     * if first page, it's null
     */
    abstract fun createCall(page: String?, pageSize: Int): LiveData<Resource<RDATA>>

    abstract fun getListFromResponseData(data: RDATA?): List<ITEM>?

    abstract fun getNextPageFromResponseData(data: RDATA?): String?

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, ITEM>) {
        loadState.postValue(ResourceLoading)

        val response = createCall(null, params.requestedLoadSize)
        response.observeOneTime {
            if (it.isLoading) {
                return@observeOneTime false
            }

            when (it) {
                is Resource.Success -> {
                    loadState.postValue(it)
                    callback.onResult(getListFromResponseData(it.data)
                            ?: emptyList(), null, getNextPageFromResponseData(it.data))
                }

                is Resource.Error -> {
                    retry = {
                        loadInitial(params, callback)
                    }

                    loadState.postValue(it)
                }
                else -> {
                    //loading state doesn't reach here
                }
            }
            return@observeOneTime true
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {
        loadState.postValue(ResourceLoading)

        val response = createCall(params.key, params.requestedLoadSize)
        response.observeOneTime {
            if (it.isLoading) {
                return@observeOneTime false
            }

            when (it) {
                is Resource.Success -> {
                    loadState.postValue(it)
                    callback.onResult(getListFromResponseData(it.data) ?: emptyList(), getNextPageFromResponseData(it.data))
                }
                is Resource.Error -> {
                    retry = {
                        loadAfter(params, callback)
                    }

                    loadState.postValue(it)
                }
                else -> {
                    //loading state doesn't reach here
                }
            }
            return@observeOneTime true
        }
    }
}