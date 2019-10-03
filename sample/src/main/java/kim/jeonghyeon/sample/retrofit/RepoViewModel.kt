package kim.jeonghyeon.sample.retrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.sample.Repo
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.RepoSearchResponse
import kim.jeonghyeon.sample.retrofit.api.GithubService
import kotlinx.coroutines.Dispatchers

class RepoViewModel : ViewModel() {

    val githubService by lazy { GithubService.create() }

    val repoList = liveData(Dispatchers.IO) {
        try {
            val a = githubService.searchRepos("a", 0, 10)
            val b = githubService.searchRepos("b", 0, 10)
            val c = githubService.searchRepos("c", 0, 10)
            val d = githubService.searchRepos("d", 0, 10)

            mutableListOf<Repo>()
                .apply { addAll(a.items) }
                .apply { addAll(b.items) }
                .apply { addAll(c.items) }
                .apply { addAll(d.items) }
                .let { emit(it) }
        } catch (e: Exception) {
            error.postValue(e.message)
        }
    }

    val error = MutableLiveData<String>()

    fun loadRepoWithLiveData(): LiveData<Resource<RepoSearchResponse>> {
        return githubService.searchReposLiveData("a", 0, 10)
    }
}