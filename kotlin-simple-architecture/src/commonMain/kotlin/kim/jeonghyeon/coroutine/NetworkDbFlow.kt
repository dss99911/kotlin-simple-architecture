package kim.jeonghyeon.coroutine

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceFlow
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 *
 * call db at first time
 * if should fetch api, cancel db flow. and call api. then re collect db flow
 * if no need to fetch api, keep collect db flow.
 * if db error, keep collect db flow to receive signal
 *
 * @param loadFromDb : flow should emit success or error only
 * @param shouldFetch : this is called just one time at first
 * @param callApi : call api
 * @param saveResponse : save from response, after save, load db again and emit result
 */
fun <RESULT, RESPONSE> networkDbFlow(
    loadFromDb: () -> ResourceFlow<RESULT>,
    shouldFetch: (dbResult: RESULT) -> Boolean,
    callApi: suspend () -> RESPONSE,
    saveResponse: suspend (RESPONSE) -> Unit
): ResourceFlow<RESULT> = flow {
    var isInitialized = false
    emit(Resource.Loading())
    //todo currently this is invoked on Main dispatcher, consider how to to on IO dispatcher on multimplatform.

    loadFromDb().collectAndCancel(condition = {
        if (it.isError()) {
            emit(it)
            return@collectAndCancel false
        }

        if (isInitialized || !shouldFetch(it.data())) {
            isInitialized = true
            emit(it)
            return@collectAndCancel false
        }

        return@collectAndCancel true
    }) {

        listenChannel { channel ->
            try {
                val response = callApi()
                saveResponse(response)
                loadFromDb().collect {
                    emit(it)
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                Resource.Error(UnknownResourceError(e)) {
                    channel.offer(Unit)
                }.let { emit(it) }
            }
        }
    }
}



