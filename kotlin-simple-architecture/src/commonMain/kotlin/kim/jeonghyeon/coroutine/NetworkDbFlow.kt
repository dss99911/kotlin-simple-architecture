package kim.jeonghyeon.coroutine

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceFlow
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

//todo this is not yet tested
fun <RESULT, RESPONSE> networkDbFlow(
    loadFromDb: () -> ResourceFlow<RESULT>,
    shouldFetch: (dbResult: RESULT, isInitialized: Boolean) -> Boolean,
    callApi: suspend () -> RESPONSE,
    saveResponse: suspend (RESPONSE) -> Unit
): ResourceFlow<RESULT> = flow {
    var isInitialized = false
    val dbFlow = loadFromDb()

    dbFlow.filter {
        if (it.isSuccess()) {
            //todo change to successData
            val needFetch = shouldFetch(it.data(), isInitialized)
            if (needFetch) {
                isInitialized = true
                val error = checkError { saveResponse(callApi()) }
                //if it's success. db observer will load data again. and flow will collect it.
                if (error?.isError() == true) {
                    emit(error!!)
                }
            }
            !needFetch//if need fetch, doesn't show db data. and call api.

            //todo test if error occurs. when retry it is working or not.
        } else true //if error, let user to retry. loadFromDb will be called again.

    }.collect {
        emit(it)
    }
}

/**
 * if success. data will be saved and flow will be updated by db changes observer
 * if cancel, just ignore.
 * if error. let user to see error and retry.
 */
private suspend fun checkError(callAndSave: suspend () -> Unit): Resource<Nothing>? = try {
    callAndSave()
    null
} catch (e: CancellationException) {
    //if cancel. then ignore it
    //todo check if cancel is working
    null
} catch (e: Exception) {
    //todo check if this is working
    val context = coroutineContext
    Resource.Error(UnknownResourceError(e)) {
        CoroutineScope(context).launch {
            callAndSave()
        }
    }
}