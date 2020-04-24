package kim.jeonghyeon.androidlibrary.architecture.paging

import androidx.paging.PagedList
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveState


/**
 * this is the model only for paging
 * you can check state of loading,
 * you can retry if loading is failed
 * you can refresh to load the data again
 */
data class Listing<T>(
    val data: LiveObject<PagedList<T>>,
    val initState: LiveState,//first api call state
    val afterState: LiveState,//call state after first api
    val isEmpty: LiveObject<Boolean>,//after api call, if there is no item, true.
    val refresh: () -> Unit//invalidate and load again
)