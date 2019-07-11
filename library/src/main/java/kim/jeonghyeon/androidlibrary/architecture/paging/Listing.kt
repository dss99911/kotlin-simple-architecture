package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState


/**
 * this is the model only for paging
 * you can check state of loading,
 * you can retry if loading is failed
 * you can refresh to load the data again
 */
data class Listing<T>(
        val data: LiveData<PagedList<T>>,
        val loadState: LiveData<ResourceState>,
        val retry: () -> Unit,//it will work only if load is failed
        val refresh: () -> Unit//invalidate and load again
)