package kim.jeonghyeon.kotlinusecase.collection

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * thread-safe
 * prevent ConcurrentModificationException
 */
fun CopyOnWriteArrayListExample() {
    CopyOnWriteArrayList<String>()
}

/**
 * thread-safe
 * prevent ConcurrentModificationException
 */
fun ConcurrentHashMapExample() {
    ConcurrentHashMap<String, String>()
}