@file:Suppress("RedundantExplicitType", "unused")

package kim.jeonghyeon.kotlinusecase.iterator

class IterableExample {

    class ArrayFunction {
        fun contentToString() {
            val array = arrayOf("a", "b", "c")
            println(array.toString())  // JVM implementation: type-and-hash gibberish -> [Ljava.lang.String;@1f32e575
            println(array.contentToString()) // -> [a, b, c]
        }
    }

    class ListFunction {

        /**
         * Array-like List instantiation functions
         */
        fun arrayLike() {
            val squares = List(10) { index -> index * index }
            val mutable = MutableList(10) { 0 }
        }
    }

    class Extentions {
        /**
         * onEach()
         */
        fun onEachMethod() {
            arrayListOf<String>()
                .filter { true }
                .onEach { println(it) }
                .forEach { }
        }

        /**
         * groupBy : list -> Map<key,list>
         */
        fun groupByMethod() {
            val groupBy = arrayListOf<String>()
                .groupBy { it.first() }
        }

        /**
         * groupingBy : get count for each
         */
        fun groupingByMethod() {
            val groupBy = arrayListOf<String>()
                .groupingBy(String::first).eachCount()
        }
    }

}
