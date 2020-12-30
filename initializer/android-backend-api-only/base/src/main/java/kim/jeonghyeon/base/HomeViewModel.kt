package kim.jeonghyeon.base

import base.generated.net.create


class HomeViewModel(val api: GreetingApi = api()) {
    suspend fun getData(): Data {
        client.create<GreetingApi>("")
        return api.greeting()
    }
}