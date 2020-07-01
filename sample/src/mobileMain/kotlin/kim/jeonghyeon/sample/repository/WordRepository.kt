package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.coroutine.networkDbFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.type.ResourceFlow

interface WordRepository {
    fun getWord(): ResourceFlow<List<Word>>
    suspend fun insertWord(word: String)
}

var fetchedWordApi = false

class WordRepositoryImpl(val api: SimpleApi, val query: WordQueries) : WordRepository {
    override fun getWord(): ResourceFlow<List<Word>> = networkDbFlow(
        loadFromDb = { query.selectAll().asListFlow() },
        shouldFetch = { !fetchedWordApi },//call first time after app started
        callApi = { api.getWords().also { fetchedWordApi = true } },
        saveResponse = {
            query.deleteAll()
            it.forEach { query.insert(it) }
        }
    )

    override suspend fun insertWord(word: String) {
        api.addWord(word)
        query.insert(word)
    }
}