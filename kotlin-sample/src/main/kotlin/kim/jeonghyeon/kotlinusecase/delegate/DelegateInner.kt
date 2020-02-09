package kim.jeonghyeon.kotlinusecase.delegate


/**
 * sometimes, we just want to use method of inner field
 * in that case, we don't need to define methods again on the class. just delegate it.
 */
class DelegatingCollection<T>(innerList: Collection<T>) : Collection<T> by innerList

fun main() {
    DelegatingCollection(arrayListOf("d")).size
}