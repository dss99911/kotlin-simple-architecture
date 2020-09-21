package kim.jeonghyeon.jvm.type

//todo delete?
class CacheList<E>(private val limit: Int) {
    val list = ArrayList<E>()

    fun add(item: E) {
        list.add(item)
        repeat(list.size - limit) {
            list.removeAt(0)
        }
    }

    fun getList() = list.toList()

}