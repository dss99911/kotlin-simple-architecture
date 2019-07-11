package kim.jeonghyeon.androidlibrary.testutil
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.junit.Assert

object ObjectComparator {
    fun compare(expected: Any?, actual: Any?) {
        if (expected == actual) {
            return
        }

        if (expected == null && actual == null) {
            return
        }

        if (expected == null && actual != null) {
            error("expected null, actual not null")
        }

        if (expected != null && actual == null) {
            error("expected not null, actual null")
        }

        val gson = GsonBuilder().serializeNulls().create()
        compare(gson.toJsonTree(expected), gson.toJsonTree(actual))
    }

    private fun compare(expected: JsonElement, actual: JsonElement) {
        if (expected.isJsonObject && actual.isJsonObject) {
            val expectedObj = expected.asJsonObject
            val actualObj = actual.asJsonObject


            Assert.assertEquals(expectedObj.size(), actualObj.size())

            expectedObj.keySet().forEach {
                compare(expectedObj[it], actualObj[it])
            }
            return
        }

        if (expected.isJsonArray && actual.isJsonArray) {
            val expectedArray = expected.asJsonArray
            val actualArray = actual.asJsonArray
            Assert.assertEquals(expectedArray.size(), actualArray.size())

            expectedArray.forEachIndexed { index, jsonElement ->
                compare(jsonElement, actualArray[index])
            }
            return
        }

        if (expected.isJsonNull && actual.isJsonNull) {
            return
        }

        if (expected.isJsonNull) {
            error("expected null")
        }

        if (actual.isJsonNull) {
            error("actual null")
        }

        Assert.assertEquals(expected.asString, actual.asString)
    }
}
