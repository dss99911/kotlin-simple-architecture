package kim.jeonghyeon.client

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


actual fun dispatcherViewModel(): CoroutineDispatcher = Dispatchers.Main.immediate