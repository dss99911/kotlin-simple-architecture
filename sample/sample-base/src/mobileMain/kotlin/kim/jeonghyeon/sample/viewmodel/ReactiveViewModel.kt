package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.*
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
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
 * I suggest to compare the [NoReactiveViewModel] which is not reactive but same business logic
 *
 * Opinion
 * - currently supporting various use case, complicated custom operator is required.
 * - if operator can be get simpler, it's good to use fully reactive way
 * - but, as it's not simple for now, it seems better to mix both as both has merit.
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
     *
     *   before list is loaded, ui is shown with empty data
     *   because, [initStatus] is null at first time.
     *   and then [list] get active by [collect] after that, initFlow's api call working.
     *   in order not to show ui before list is loaded, initFlow's api call should be active before list active.
     *   so, this should be handled by different approach.
     *   but, this viewModel's purpose is just showing reactive way. so keep this way.
     *
     */
    @OptIn(FlowPreview::class)
    val list: DataFlow<List<String>> by add {
        flowsToSingle (
            initFlow
                .mapToResource { api.getWords() }
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