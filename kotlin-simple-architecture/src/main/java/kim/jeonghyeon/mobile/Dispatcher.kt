package kim.jeonghyeon.mobile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun dispatcherUI(): CoroutineDispatcher = Dispatchers.Main