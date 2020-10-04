package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.mapToResource
import kim.jeonghyeon.sample.di.serviceLocator


/**
 * todo add filtering function on word list
 */
class ReactiveViewModel(private val api: PreferenceApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.preferenceApi)

    private val KEY = "someKey"

    val click by add { DataFlow<Unit>() }
    val input by add { DataFlow<String>().withSource(result) }

    //todo how to call api, when this is idle only?
    val clickResult: DataFlow<String> by add {
        click.mapToResource {
            val text = input.value ?: error("please input")
            api.setString(KEY, text)
            text
        }.toDataFlow(status)
    }

    val result by add {
        dataFlow<String> {
            setValue(api.getString(KEY)?:"")
        }.withSource(clickResult)
    }
}