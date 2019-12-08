package kim.jeonghyeon.androidlibrary.deprecated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import kim.jeonghyeon.androidlibrary.architecture.livedata.*

object LiveDataUtil {


    fun <A, B> mergeResource(a: ResourceLiveData<A>, b: ResourceLiveData<B>): ResourceLiveData<Unit> =
        mergeResource(a, b) { _, _ ->
            Resource.Success(Unit)
        }
    /**
     * merge all live data : activate all the live data, so that, call in parallel
     * and if it is error, return UPINeedRetryError, if not, call onSuccess param
     */
    fun <A, B, OUT> mergeResource(a: ResourceLiveData<A>, b: ResourceLiveData<B>, onSuccess: (A, B) -> Resource<OUT>): ResourceLiveData<OUT> {
        val startA = MutableLiveData(null)
        val startB = MutableLiveData(null)
        return startA.switchMap { a }
            .mergeNotNull(startB.switchMap { b }) { rA, rB ->
                if (rA.isLoading || rB.isLoading) Resource.Loading
                else if (rA is Resource.Error) {
                    rA
                } else if (rB is Resource.Error) {
                    rB
                } else if (rA is Resource.Success && rB is Resource.Success) {
                    onSuccess(rA.data, rB.data)
                } else {
                    val notSuccess = if (rA is Resource.Success) rB else rA
                    notSuccess as Resource<OUT>
                }
            }

    }

    /**
     * merge all live data : activate all the live data, so that, call in parallel
     * and if it is error, return UPINeedRetryError, if not, call onSuccess param
     */
    fun <IN, OUT> mergeResource(sources: List<ResourceLiveData<IN>>, onSuccess: (List<Resource<IN>>) -> Resource<OUT>): ResourceLiveData<OUT> {
        if (sources.isEmpty()) {
            return MutableLiveData(onSuccess(emptyList()))
        }

        val start = List(sources.size) {
            MutableLiveData(null)
        }

        return mergeAll(
            sources.mapIndexed { index, data ->
                start[index].switchMap { data }
            }.toList()
        ) { list ->
            when {
                list.any { it.isLoading } -> Resource.Loading
                list.any { it.isError } -> list.first { it is Resource.Error } as Resource<OUT>
                list.all { it.isSuccess } -> onSuccess(list)
                else -> list.first { !it.isSuccess } as Resource<OUT>
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
