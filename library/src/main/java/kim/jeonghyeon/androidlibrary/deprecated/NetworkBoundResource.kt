package kim.jeonghyeon.androidlibrary.deprecated

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.isLoading
import kim.jeonghyeon.androidlibrary.architecture.net.error.SaveFailedError
import kim.jeonghyeon.androidlibrary.deprecated.thread.AppExecutors

/**
 * use this class if there are data in both local storate and server
 * Don't inherit this class if the class has constructor parameter which is used on loadFromDB
 *
 * https://developer.android.com/jetpack/docs/guide
 */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val executors: AppExecutors) {

    protected val result =
        RetriableLiveData<Resource<ResultType>> {
            if (it.isLoading) {
                return@RetriableLiveData false
            }

            invalidate()
            return@RetriableLiveData true
        }

    /**
     * whenever state or data is changed, new resource instance is created, in order to set data and state synchronized.
     */
    val asLiveData: RetriableLiveData<Resource<ResultType>>
        get() = result

    init {
        invalidate()
    }

    /**
     * please call this only when process is completed, i.e. on success. or on error state.
     */
    protected fun invalidate() {
        result.value = Resource.Loading
        executors.executeDisk {
            val dbSource = loadFromDb()
            executors.executeMainThread {
                result.addSource(dbSource) { data ->
                    //in order to call api when observer is registered.
                    result.removeSource(dbSource)
                    if (shouldFetch(data)) {
                        fetchFromNetwork(dbSource)
                    } else {
                        result.addSource(dbSource) {
                            result.value =
                                Resource.Success(
                                    it
                                )
                        }
                    }
                }
            }
        }

    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        result.addSource(dbSource) {
            result.value =
                Resource.LoadingWithData(it)
        }

        val response = createCall()
        result.addSource(response) { resource ->
            if (resource.isLoading) {
                return@addSource
            }

            result.removeSource(response)
            result.removeSource(dbSource)

            when (resource) {
                is Resource.Success -> {
                    saveResultAndReInit(resource.data)
                }
                is Resource.Error -> {
                    result.addSource(dbSource) { newData ->
                        result.value =
                            Resource.ErrorWithData(
                                resource.error,
                                newData
                            )
                    }
                }
                else -> {
                    //loading state doesn't reach here
                }
            }
        }
    }

    @MainThread
    @SuppressLint("StaticFieldLeak")//same lifecycle
    private fun saveResultAndReInit(data: RequestType?) {
        executors.executeDisk {
            if (!saveCallResult(data)) {
                result.value = SaveFailedError().asResource()
                return@executeDisk
            }
            executors.executeMainThread {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                result.addSource(loadFromDb()) {
                    result.value =
                        Resource.Success(it)
                }
            }
        }
    }

    // Called to get the cached data from the database
    // it should notify, if the data is changed.
    @WorkerThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<Resource<RequestType>>

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType?): Boolean
}