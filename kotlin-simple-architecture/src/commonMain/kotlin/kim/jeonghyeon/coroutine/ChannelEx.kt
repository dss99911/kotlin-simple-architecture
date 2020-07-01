package kim.jeonghyeon.coroutine

import kotlinx.coroutines.channels.Channel


suspend fun listenChannel(onRepeat: suspend (Channel<Unit>) -> Unit) {

    val channel = Channel<Unit>(Channel.CONFLATED)
    channel.offer(Unit)

    try {
        for (item in channel) {
            onRepeat(channel)
        }
    } finally {
    }
}