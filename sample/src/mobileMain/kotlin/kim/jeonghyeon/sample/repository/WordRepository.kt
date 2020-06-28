package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.coroutine.networkDbFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.ResourceFlow

//todo move to sample module
interface WordRepository {
    fun getWord(): ResourceFlow<List<Word>>
    suspend fun insertWord(word: String)
}

class WordRepositoryImpl(val api: SimpleApi = serviceLocator.simpleApi, val query: WordQueries = serviceLocator.wordQueries) : WordRepository {
    override fun getWord(): ResourceFlow<List<Word>> = networkDbFlow(
        loadFromDb = { query.selectAll().asListFlow() },
        shouldFetch = { _, isInitialized -> !isInitialized },
        callApi = { api.getWords().split(",") },
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