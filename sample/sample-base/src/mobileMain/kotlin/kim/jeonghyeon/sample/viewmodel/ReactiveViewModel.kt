package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.*
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.UnknownResourceError
import kim.jeonghyeon.util.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Everything(event, user action, data) is stream.
 *
 * one stream make other streams. and multiple streams become one stream as well. it's like circuit
 *
 * this shows reactive way
 *
 * Merit
 * - it shows dependencies between stream clearly.
 *   if A is changed by B, B doesn't need to know what is affected by B. only A have to know A is changed by B
 *   so, all the code, which change A, should be near to A's definition
 *
 * Demerit
 * - various operator is required to implement stream
 *   (I created custom operator, it may get simpler in the future. but currently it looks complicated without knowledge)
 *
 * I suggest to compare the [ReactiveViewModel2] which is not reactive but same business logic
 */
class ReactiveViewModel(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi)

    val newWord by add { DataFlow<String>() }

    val click by add { DataFlow<Unit>() }

    val keyword by add { DataFlow<String>() }

    /**
     * emit status when error only. so, when type keyword, loading ui doesn't block user typing.
     */
    val fail by add { StatusFlow() }

    override val status: StatusFlow by add {
        fail.transform {
            if (it.isError()) {
                emit(it)
            }
        }.toResourceFlow()
    }

    /**
     * this flow observe the below
     *  1. inputting keyword to filter
     *  2. clicking 'add' button after inputting newWord
     *  3. init
     *
     *  [toDataFlow] is not reactive. I feel that status is subsidiary so, need to focus on data
     *   if you want fully reactive,
     *   make 3 field of ResourceFlow<List<String>> for (init, keyword, click).
     *   and let each status(initStatus, fail, status) to observe each list.
     */
    @OptIn(FlowPreview::class)
    val list: DataFlow<List<String>> by add {
        flowsToSingle (
            initFlow
                .mapToResource { api.getWordsOfKeyword("") }
                .toDataFlow(initStatus),
            keyword
                .debounce(1000)
                .mapToResource { api.getWordsOfKeyword(it) }
                .toDataFlow(fail),
            click
                .mapToResourceIfIdle {
                    api.addWord(newWord.value?: error("input word"))
                    api.getWordsOfKeyword(keyword.value?:"")
                }
                .toDataFlow(status)
        ).toDataFlow()
    }
}


/**
 * If A is changed by B, the code to change A is located on B's definition.
 * it's not reactive.
 *
 * Merit
 * - it's simple
 * - no need to know complicated flows custom operators
 *
 * Demerit
 * - difficult to know what change the data(but as it's simple, I feel it's not that difficult if business logic is not too much complicated)
 */
class ReactiveViewModel2(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi)

    val list: DataFlow<List<String>> by add { DataFlow() }
    val newWord by add { DataFlow<String>() }

    /**
     * emit status when error only. so, when type keyword, loading ui doesn't block user typing.
     */
    val fail by add {
        StatusFlow().apply {
            collectOnViewModel {
                if (it.isError()) {
                    status.setValue(it)
                }
            }
        }
    }

    val keyword by add {
        DataFlow<String>().apply {
            collectOnViewModel {
                list.loadDebounce(fail, 2000) {
                    api.getWordsOfKeyword(it)
                }
            }
        }
    }

    override fun onInit() {
        list.load(initStatus) {
            api.getWords()
        }
    }

    fun onClick() {
        list.loadInIdle(status) {
            api.addWord(newWord.value?: error("input word"))
            api.getWordsOfKeyword(keyword.value?:"")
        }
    }
}