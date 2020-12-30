package kim.jeonghyeon.base

import kim.jeonghyeon.client.BaseViewModel

class HomeViewModel(val api: GreetingApi = api<GreetingApi>()) : BaseViewModel() {
    val greeting = viewModelFlow<String>()

    override fun onInitialized() {
        greeting.load(initStatus) {
            api.greeting()
        }
    }
}