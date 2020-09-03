package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.coroutine.resourceFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

interface WordRepository {
    fun getWord(): Flow<Resource<List<Word>>>
    suspend fun insertWord(word: String)
}

var fetchedWordApi = false

class WordRepositoryImpl(/*val api: SampleApi,*/ val query: WordQueries) : WordRepository {
    override fun getWord(): Flow<Resource<List<Word>>> = resourceFlow {
        if (!fetchedWordApi) {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter serviceLocator.userRepository
            val response = serviceLocator.sampleApi.getWords()
            query.deleteAll()
            response.forEach { query.insert(it) }
            fetchedWordApi = true
        }

        query.selectAll().asListFlow().collect {
            emit(it)
        }
    }

    override suspend fun insertWord(word: String) {
        //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
        // use constructor parameter serviceLocator.userRepository
        serviceLocator.sampleApi.addWord(word)
        query.insert(word)
    }
}