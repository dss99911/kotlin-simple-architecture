package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.*
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


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
class ReactiveViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Reactive"

    val list = viewModelFlow<List<String>>()
    val newWord = viewModelFlow<String>()

    /**
     * emit status when error only. so, when type keyword, loading ui doesn't block user typing.
     */
    val keywordStatus = viewModelFlow<Status>()

    override val status: ViewModelFlow<Status> = viewModelFlow<Status>().withSource(keywordStatus.filter { it.isError() })

    val keyword = viewModelFlow<String>()

    override fun onInit() {
        list.load(initStatus) {
            api.getWords()
        }

        keyword.collectOnViewModel {
            list.loadDebounce(1000, keywordStatus) {
                api.getWordsOfKeyword(it)
            }
        }
    }

    fun onClick() {
        list.loadInIdle(status) {
            api.addWord(newWord.valueOrNull?: error("input word"))
            api.getWordsOfKeyword(keyword.valueOrNull?:"")
        }
    }
}

// TODO reactive way.
//class ReactiveViewModel2(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Reactive"
//
//    val keyword by add { viewModelFlow<String>() }
//    val newWord by add { viewModelFlow<String>() }
//    val click by add { viewModelFlow<Unit>() }
//
//    val list by add {
//        merge(
//            initFlow
//                .map {
//                    api.getWords()
//                }
//                .toData(initStatus),
//            keyword
//                .debounce(1000)
//                .mapCancelRunning {
//                    api.getWordsOfKeyword(it)
//                }
//                .toData(keywordStatus),
//            click
//                .mapInIdle {
//                    api.addWord(newWord.valueOrNull?: error("input word"))
//                    api.getWordsOfKeyword(keyword.valueOrNull?:"")
//                }
//                .toData(status)
//        )
//    }
//
//    val keywordStatus by add { viewModelFlow<Status>() }
//
//    override val status: MutableSharedFlow<Status> by add {
//        //emit status when error only. so, when type keyword, loading ui doesn't block user typing.
//        keywordStatus.filter { it.isError() }.toStatus()
//    }
//
//
//}

/**
 * TODO arrange the comment
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
 * also recommend to compare with [ApiDbViewModel] which use Repository
 *
 * Opinion
 * - to support various use case requires complicated custom flow operator.
 * - so, use Flow operator only on the case below, otherwise, use coroutine suspend for singe call like calling api
 *      - changeable data like database
 *      - transforming data by map {}
 *      - combine multiple flow
 */

/**
 * this flow observe the below
 *  1. inputting keyword to filter
 *  2. clicking 'add' button after inputting newWord
 *  3. init
 *
 *  [toDataFlow] is not reactive. I think that status is subsidiary so, need to focus on data
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
 *   this sample shows complicated Flow use case.
 *   the reason not to use Repository, is that if use repository, it's very simple, so can't use much operators.
 *   you can refer [ApiDbViewModel] for repository use case
 *
 */