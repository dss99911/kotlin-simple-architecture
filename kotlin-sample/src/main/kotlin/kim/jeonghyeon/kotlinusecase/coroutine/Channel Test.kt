package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * contains channel
 */
fun actorMethod() {
    runBlocking {
        val actor = actor<String> {
            for (msg in channel) {
                println(msg)
            }
        }
        actor.send("test")
        actor.close()

    }
}

fun produceMethod() {
    runBlocking {
        val produce = produce<String> {
            channel.send("test")
        }
        produce.receive()
        produce.cancel()
    }

}