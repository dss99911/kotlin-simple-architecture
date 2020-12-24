package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.client.*
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

interface WordRepository {
    val word: Flow<List<Word>>
    suspend fun insertWord(word: String)
}

var fetchedWordApi = false

class WordRepositoryImpl(val api: SampleApi, val query: WordQueries) : WordRepository {
    override val word: Flow<List<Word>> = shareFlow(MainScope()) {
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