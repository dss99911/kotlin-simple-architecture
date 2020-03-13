package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.toLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseNetworkDataSourceFactory<ITEM, RDATA : Any>(private val pageSize: Int) : DataSource.Factory<String, ITEM>() {

    private val sourceLiveData = LiveObject<BaseNetworkDataSource<ITEM, RDATA>>()

    /**
     * if first page, it's 1
     */
    abstract suspend fun createCall(page: Int, pageSize: Int): RDATA

    abstract fun getListFromResponseData(data: RDATA): List<ITEM>

    /**
     * if next page doesn't exist, returns null
     */
    abstract fun getNextPageFromResponseData(data: RDATA, currentPage: Int, currentRequestedPageSize: Int): Int?


    override fun create(): DataSource<String, ITEM> = object : BaseNetworkDataSource<ITEM, RDATA>(pageSize) {
        override suspend fun createCall(page: Int, pageSize: Int): RDATA =
            this@BaseNetworkDataSourceFactory.createCall(page, pageSize)

        override fun getListFromResponseData(data: RDATA): List<ITEM> =
                this@BaseNetworkDataSourceFactory.getListFromResponseData(data)

        override fun getNextPageFromResponseData(
            data: RDATA,
            currentPage: Int,
            currentRequestedPageSize: Int
        ): Int? = this@BaseNetworkDataSourceFactory.getNextPageFromResponseData(data, currentPage, currentRequestedPageSize)

    }.also { sourceLiveData.postValue(it) }

    val asListing: Listing<ITEM>
        get() {
            val livePagedList = toLiveData(pageSize)
            return Listing(
                data = livePagedList.asLiveObject(),
                    loadState = sourceLiveData.switchMap { it.loadState },
                    retry = { sourceLiveData.value?.retryFailed() },
                    refresh = { sourceLiveData.value?.invalidate() }
            )
        }
}

private abstract class BaseNetworkDataSource<ITEM, RDATA : Any>(val pageSize: Int) : PageKeyedDataSource<String, ITEM>() {

    val loadState = LiveState()

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    fun retryFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }


    abstract suspend fun createCall(page: Int, pageSize: Int): RDATA

    abstract fun getListFromResponseData(data: RDATA): List<ITEM>

    abstract fun getNextPageFromResponseData(data: RDATA, currentPage: Int, currentRequestedPageSize: Int): Int?

    /**
     * todo at first time, it calls pageSize * 3. and then call pageSize. need to check pageSize * 3 is fixed amount
     * now error occurs.
     * on loadAfter, how do we know that, at first time, it is
     */
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, ITEM>) {
        GlobalScope.launch(Dispatchers.Main) {
            loadResource(loadState, {
                createCall(1, params.requestedLoadSize)
            }, {
                it.onSuccess {
                    @Suppress("UNCHECKED_CAST")
                    it as RDATA
                    callback.onResult(
                        getListFromResponseData(it),
                        null,
                        getNextPageFromResponseData(it, 1, params.requestedLoadSize)?.toString()
                    )
                }.onError {
                    retry = {
                        loadInitial(params, callback)
                    }
                }
            })
        }

    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {
        GlobalScope.launch(Dispatchers.Main) {
            loadResource(loadState, {
                createCall(params.key.toInt(), params.requestedLoadSize)
            }, {
                it.onSuccess {
                    @Suppress("UNCHECKED_CAST")
                    it as RDATA
                    callback.onResult(
                        getListFromResponseData(it),
                        getNextPageFromResponseData(
                            it,
                            params.key.toInt(),
                            params.requestedLoadSize
                        ).toString()
                    )
                }.onError {
                    retry = {
                        loadAfter(params, callback)
                    }
                }
            })
        }
    }
}