package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.coroutine.networkDbFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.type.ResourceFlow

//todo move to sample module
interface WordRepository {
    fun getWord(): ResourceFlow<List<Word>>
    suspend fun insertWord(word: String)
}

var fetchWordApi = true

class WordRepositoryImpl(val api: SimpleApi, val query: WordQueries) : WordRepository {
    override fun getWord(): ResourceFlow<List<Word>> = networkDbFlow(
        loadFromDb = { query.selectAll().asListFlow() },
        shouldFetch = { _, isInitialized -> fetchWordApi.also { fetchWordApi = false } },//call first time after app started
        callApi = { api.getWords() },
        saveResponse = {
            //todo check if insert one by one make flow receive each flow. or just last one.

            it.forEach { query.insert(it) }
        }
    )

    override suspend fun insertWord(word: String) {
        api.addWord(word)
        query.insert(word)
    }
}