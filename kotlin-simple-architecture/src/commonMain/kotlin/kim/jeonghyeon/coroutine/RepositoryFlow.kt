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
 * @param loadFromDb : load db
 * @param shouldFetch : this is called just one time at first
 * @param callApi : call api
 * @param saveResponse : save from response, after save, load db again and emit result
 */
fun <RESULT, RESPONSE> repositoryFlow(
    loadFromDb: () -> ResourceFlow<RESULT>,
    shouldFetch: (dbResult: RESULT) -> Boolean,
    callApi: suspend () -> RESPONSE,
    saveResponse: suspend (RESPONSE) -> Unit
): ResourceFlow<RESULT> = flow {
    var isInitialized = false
    var dbData: RESULT? = null
    //todo currently this is invoked on Main dispatcher, consider how to do on IO dispatcher on multimplatform.

    loadFromDb().collectAndCancel(condition = {
        if (it.isLoading() || it.isStart()) {
            return@collectAndCancel false
        }
        if (it.isError()) {
            emit(it)
            return@collectAndCancel false
        }

        dbData = it.successData()
        @Suppress("UNCHECKED_CAST")
        if (isInitialized || !shouldFetch(dbData as RESULT)) {
            isInitialized = true
            emit(it)
            return@collectAndCancel false
        }

        //call api after cancelling this flow
        emit(Resource.Loading(dbData))
        return@collectAndCancel true
    }) {
        observeTrial { retry ->
            try {
                val response = callApi()
                saveResponse(response)
                loadFromDb().collect {
                    emit(it)
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                val error = Resource.Error(UnknownResourceError(e), dbData) {
                    retry()
                }
                emit(error)
            }
        }
    }
}



