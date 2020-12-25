package kim.jeonghyeon.base

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow

class HomeViewModel(val api: GreetingApi) : BaseViewModel() {
    constructor(): this(api<GreetingApi>())

    val greeting by add { DataFlow<String>() }

    override fun onInitialized() {
        greeting.load(initStatus) {
            api.greeting()
        }
    }
}