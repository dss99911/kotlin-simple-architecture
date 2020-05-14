package kim.jeonghyeon.common

interface StorageApi {
    suspend fun getKeys(): List<String>

    suspend fun getValue(key: String): String
}