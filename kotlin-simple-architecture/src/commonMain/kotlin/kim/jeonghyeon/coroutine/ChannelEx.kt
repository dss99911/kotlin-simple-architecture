package kim.jeonghyeon.coroutine

import kotlinx.coroutines.channels.Channel


suspend fun observeTrial(onRepeat: suspend (retry: () -> Unit) -> Unit) {

    val channel = Channel<Unit>(Channel.CONFLATED)
    channel.offer(Unit)

    try {
        for (item in channel) {
            onRepeat {
                channel.offer(Unit)
            }
        }
    } finally {
    }
}