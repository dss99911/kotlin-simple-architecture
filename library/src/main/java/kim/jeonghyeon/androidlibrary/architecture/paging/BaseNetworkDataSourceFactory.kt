package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.toLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val PAGE_FIRST = 1

abstract class BaseNetworkDataSourceFactory<ITEM, RDATA : Any>(private val pageSize: Int) :
    DataSource.Factory<String, ITEM>() {

    private val sourceLiveData = LiveObject<BaseNetworkDataSource<ITEM, RDATA>>()

    /**
     * if first page, it's [PAGE_FIRST]
     */
    abstract suspend fun createCall(page: Int, pageSize: Int): RDATA

    abstract fun getListFromResponseData(data: RDATA, requestPage: Int): List<ITEM>

    /**
     * if next page doesn't exist, returns null
     */
    abstract fun getNextPageFromResponseData(
        data: RDATA,
        currentPage: Int,
        currentRequestedPageSize: Int
    ): Int?


    override fun create(): DataSource<String, ITEM> =
        object : BaseNetworkDataSource<ITEM, RDATA>(pageSize) {
            override suspend fun createCall(page: Int, pageSize: Int): RDATA =
                this@BaseNetworkDataSourceFactory.createCall(page, pageSize)

            override fun getListFromResponseData(data: RDATA, requestPage: Int): List<ITEM> =
                this@BaseNetworkDataSourceFactory.getListFromResponseData(data, requestPage)

            override fun getNextPageFromResponseData(
                data: RDATA,
                currentPage: Int,
                currentRequestedPageSize: Int
            ): Int? = this@BaseNetworkDataSourceFactory.getNextPageFromResponseData(
                data,
                currentPage,
                currentRequestedPageSize
            )

        }.also { sourceLiveData.postValue(it) }

    val asListing: Listing<ITEM>
        get() {
            val livePagedList = toLiveData(pageSize)
            return Listing(
                data = livePagedList.asLiveObject(),
                initState = sourceLiveData.switchMap { it.initState },
                afterState = sourceLiveData.switchMap { it.afterState },
                isEmpty = sourceLiveData.switchMap { it.isEmpty },
                refresh = { sourceLiveData.value?.invalidate() }
            )
        }
}

private abstract class BaseNetworkDataSource<ITEM, RDATA : Any>(val pageSize: Int) :
    PageKeyedDataSource<String, ITEM>() {

    val scope = MainScope()
    val initState = LiveState()
    val afterState = LiveState()
    val isEmpty = LiveObject<Boolean>()

    abstract suspend fun createCall(page: Int, pageSize: Int): RDATA

    abstract fun getListFromResponseData(data: RDATA, requestPage: Int): List<ITEM>

    abstract fun getNextPageFromResponseData(
        data: RDATA,
        currentPage: Int,
        currentRequestedPageSize: Int
    ): Int?

    /**
     * todo at first time, it calls pageSize * 3. and then call pageSize. need to check pageSize * 3 is fixed amount
     * now error occurs.
     * on loadAfter, how do we know that, at first time, it is
     */
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, ITEM>
    ) {
        scope.launch {
            scope.loadResource(initState, {
                createCall(PAGE_FIRST, params.requestedLoadSize)
            }, {

                if (it.isSuccess()) {
                    @Suppress("UNCHECKED_CAST")
                    val data = it.get() as RDATA
                    val list = getListFromResponseData(data, PAGE_FIRST)
                    val nextPage =
                        getNextPageFromResponseData(data, PAGE_FIRST, params.requestedLoadSize)
                    callback.onResult(list, null, nextPage?.toString())

                    isEmpty.value = list.isEmpty() && nextPage == null
                } else {
                    isEmpty.value = false
                }

                it
            })
        }

    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ITEM>) {
        val requestPage = params.key.toInt()
        scope.launch {
            scope.loadResource(afterState, {
                createCall(requestPage, params.requestedLoadSize)
            }, {
                it.onSuccess {
                    @Suppress("UNCHECKED_CAST")
                    it as RDATA
                    callback.onResult(
                        getListFromResponseData(it, requestPage),
                        getNextPageFromResponseData(
                            it,
                            requestPage,
                            params.requestedLoadSize
                        ).toString()
                    )
                }
                it
            })
        }
    }
}