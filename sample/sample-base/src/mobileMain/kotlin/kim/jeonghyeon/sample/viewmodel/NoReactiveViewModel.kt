package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.StatusFlow
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

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
class NoReactiveViewModel(private val api: SampleApi) : SampleViewModel() {

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
                list.loadDebounce(fail, 1000) {
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