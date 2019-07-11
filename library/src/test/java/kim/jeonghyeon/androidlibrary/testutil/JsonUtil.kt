package kim.jeonghyeon.androidlibrary.testutil

import okio.Okio

object JsonUtil {

    /**
     * load from api-response folder
     */
    fun getJsonFromFile(fileName: String): String? {
        val inputStream = javaClass.classLoader!!
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        return source.readString(Charsets.UTF_8)
    }
}