package kim.jeonghyeon.client

import kotlinx.coroutines.*
import kotlin.test.Test

/**
 * TODO : make proper format of test code
 *  consideration
 *  - upstream
 *      - cold flow, hot flow
 *      - SUSPEND, DROP_OLDEST, DROP_LATEST
 *      - emit interval : multiple directly(without delay), multiple indirect, just single, none
 *  - downstream
 *      - subscriber count : multiple, single, none
 *      - processing time : long, short
 *  - scope
 *      - single thread, multiple thread
 *  - exception
 *
 */
class MutableSharedFlowExKtTest {
    @Test
    fun singleThread_directEmit() {
        //single thread + emit multiple value directly
//
//        val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
//        val a = flow<Int> {
//            emit(1)
//            emit(2)
//            emit(3)
//            emit(4)
//            emit(5)
//        }.transformResourceFromFlow(scope, null, ) {
//            println("map $it")
//            delay(500)
//            if (it.toString().toInt() >= 4) {
//                error("some error")
//            }
//            emit(it)
//        }
//        println("start")
//        scope.launch {
//            a.collect {
//                delay(500)
//                println(it)
//                if (it.isError()) {
//                    delay(100)
//                    it.retry()
//                }
//            }
//        }
//
//        runBlocking {
//            delay(20000)
//            println("finished")
//        }
    }
//
//    @Test
//    fun a() {
//        //error
//        //shared flow
//        val flowSingle = flowSharedSingle<Int>()
//        GlobalScope.launch {
//
//            val mapResource = flowSingle
//                .mapToResource(GlobalScope, name = "name1", jobPolicy = FlowJobPolicy.IN_IDLE) {
//                    it.toString()
//                }.mapResource("name2") {
//
//                    it + "a"
//                }.mapResource(
//                    GlobalScope,
//                    name = "name3",
//                    jobPolicy = FlowJobPolicy.CANCEL_RUNNING
//                ) { value: String ->
//                    value + "b"
//                }
//            mapResource.collect {
//                println("result : $it")
//
//            }
//            mapResource.collect {
//                println("result2 : $it")
//
//            }
//        }
//
//        GlobalScope.launch {
//            flowSingle.tryEmit(2)
//            flowSingle.tryEmit(3)
//        }
//
//        runBlocking {
//            delay(10000)
//        }
//
//    }
}