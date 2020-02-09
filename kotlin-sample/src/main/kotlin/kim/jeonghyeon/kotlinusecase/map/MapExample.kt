@file:Suppress("RedundantExplicitType", "unused", "RemoveExplicitTypeArguments")

package kim.jeonghyeon.kotlinusecase.map

class MapExample {
    /**
     * Copy Map
     * toMap() : copy
     * toMutableMap()
     */
    fun copy() {
        mapOf<String, String>().toMap().toMutableMap()

    }

    /**
     * Operator +, -
     * put and remove and make new instance
     */
    fun plusMinus() {
        val map = mapOf<String, String>() + mapOf()
        val map1 = mapOf<String, String>() + ("adsf" to "adsf")
        val map2 = map - "a"
    }

    /**
     * when get value, throw exception or get default value
     */
    fun getValue() {
        val map = mapOf("key" to 42)

        //return nullable value
        val i = map["key"]

        // returns non-nullable Int value 42

        val value: Int = map.getValue("key")

        val mapWithDefault = map.withDefault { k -> k.length }
        // returns 4
        val value2 = mapWithDefault.getValue("key2")

        // map.getValue("anotherKey") // <- this will throw NoSuchElementException
    }
}