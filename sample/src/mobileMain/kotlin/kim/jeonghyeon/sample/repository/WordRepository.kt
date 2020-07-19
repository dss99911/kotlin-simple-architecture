package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.api.SimpleApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

interface WordRepository {
    fun getWord(): Flow<List<Word>>
    suspend fun insertWord(word: String)
}

var fetchedWordApi = false

class WordRepositoryImpl(val api: SimpleApi, val query: WordQueries) : WordRepository {

    override fun getWord(): Flow<List<Word>> = flow {
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