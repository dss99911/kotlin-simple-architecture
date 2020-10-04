package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.client.ResourceFlow
import kim.jeonghyeon.client.resourceFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect

interface WordRepository {
    fun getWord(): ResourceFlow<List<Word>>
    suspend fun insertWord(word: String)
}

var fetchedWordApi = false

class WordRepositoryImpl(val api: SampleApi, val query: WordQueries) : WordRepository {
    override fun getWord(): ResourceFlow<List<Word>> = resourceFlow(CoroutineScope(Dispatchers.Main)) {
        if (!fetchedWordApi) {
            val response = api.getWords()
            query.deleteAll()
            response.forEach { query.insert(it) }
            fetchedWordApi = true
        }

        query.selectAll().asListFlow().collect {
            emit(it)
        }
    }

    override suspend fun insertWord(word: String) {
        api.addWord(word)
        query.insert(word)
    }
}