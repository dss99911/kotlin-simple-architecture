package kim.jeonghyeon.androidlibrary.sample.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kim.jeonghyeon.androidlibrary.architecture.net.body
import kim.jeonghyeon.androidlibrary.sample.Repo
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.GithubService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

class RepoViewModel : ViewModel() {

    val githubService by lazy { GithubService.create() }

    fun loadRepo(): List<Repo> {
        val result = MutableLiveData<List<Repo>>()
        GlobalScope.async {
            try {
                val a = githubService.searchRepos("a", 0, 10).body()
                val b = githubService.searchRepos("b", 0, 10).body()
                val c = githubService.searchRepos("c", 0, 10).body()
                val d = githubService.searchRepos("d", 0, 10).body()

                result.value = mutableListOf<Repo>()
                    .apply { addAll(a?.items ?: return@apply) }
                    .apply { addAll(b?.items ?: return@apply) }
                    .apply { addAll(c?.items ?: return@apply) }
                    .apply { addAll(d?.items ?: return@apply) }

            } catch (e: Exception) {
                //todo process error
            }
        }

    }

    fun Job.asLiveData() {

    }
}