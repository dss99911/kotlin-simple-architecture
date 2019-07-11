package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.NeedRetryError

object LiveDataUtil {


    /**
     * merge all live data : activate all the live data, so that, call in parallel
     * and if it is error, return UPINeedRetryError, if not, call onSuccess param
     */
    fun <A, B, OUT> mergeResourceRetry(a: LiveData<Resource<A>>, b: LiveData<Resource<B>>, onSuccess: (Resource<A>, Resource<B>) -> Resource<OUT>): LiveData<Resource<OUT>> {
        val startA = mutableLiveDataOf(null)
        val startB = mutableLiveDataOf(null)
        return startA.switchMap { a }
                .mergeNotNull(startB.switchMap { b }) { rA, rB ->
                    if (rA.isLoading || rB.isLoading) Resource.loading(null as OUT?)
                    else if (rA.isError || rB.isError) {
                        Resource.error(NeedRetryError {
                            if (rA.isError) {
                                startA.repeat()
                            }
                            if (rB.isError) {
                                startB.repeat()
                            }
                        })
                    } else if (rA.isSuccess && rB.isSuccess) {
                        onSuccess(rA, rB)
                    } else {
                        val notSuccess = if (rA.isSuccess) rB else rA
                        Resource(null as OUT?, notSuccess.state)
                    }
                }

    }

    /**
     * merge all live data : activate all the live data, so that, call in parallel
     * and if it is error, return UPINeedRetryError, if not, call onSuccess param
     */
    fun <IN, OUT> mergeResourceRetry(sources: List<LiveData<Resource<IN>>>, onSuccess: (List<Resource<IN>>) -> Resource<OUT>): LiveData<Resource<OUT>> {
        if (sources.isEmpty()) {
            return mutableLiveDataOf(onSuccess(emptyList()))
        }

        val start = List<MutableLiveData<IN>>(sources.size) {
            mutableLiveDataOf(null)
        }

        return LiveDataUtil.mergeAll(
                sources.mapIndexed { index, data ->
                    start[index].switchMap { data }
                }.toList()) { list ->
            when {
                list.any { it.isLoading } -> Resource.loading(null as OUT?)
                list.any { it.isError } -> Resource.error(NeedRetryError {
                    list.forEachIndexed { index, resource ->
                        if (resource.isError) {
                            start[index].repeat()
                        }
                    }
                })
                list.all { it.isSuccess } -> onSuccess(list)
                else -> Resource(null as OUT?, list.first { !it.isSuccess }.state)
            }
        }
    }


    fun <T, OUT> mergeAll(sources: List<LiveData<T>>, observer: (all: List<T>) -> OUT?): LiveData<OUT> {
        val result = MediatorLiveData<OUT>()
        sources.forEach { source ->
            result.addSource(source) { _ ->
                if (sources.all { it.value != null }) {
                    result.value = observer(
                            sources.map { it.value!! }.toList()
                    )
                }

            }
        }

        return result
    }
}
