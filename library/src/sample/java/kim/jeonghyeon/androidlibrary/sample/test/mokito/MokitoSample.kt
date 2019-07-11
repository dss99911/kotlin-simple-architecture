package kim.jeonghyeon.androidlibrary.sample.test.mokito

import androidx.lifecycle.Observer
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.sample.retrofit.api.GithubService
import kim.jeonghyeon.androidlibrary.sample.test.api.Home
import kim.jeonghyeon.androidlibrary.testutil.argumentCaptor
import kim.jeonghyeon.androidlibrary.testutil.mock
import kim.jeonghyeon.androidlibrary.testutil.on
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.mockito.Mockito.*

class MokitoSample {
    private val observer: Observer<Resource<Boolean>> = mock()

    fun a() {
        val service:GithubService = mock()
        on(service.searchRepos(any(), any(), any())).thenReturn(null)

        verify(observer).onChanged(null)// check if the method is invoked with the parameter
        verify(observer, times(2)).onChanged(any())
        verifyNoMoreInteractions(observer)
        verify(observer, never()).onChanged(any())
    }

    /**
     * on verify, take the value, and check if the value is correct or not
     */
    fun captor() {
        val inserted = argumentCaptor<List<Home>>()
        // empty list is a workaround for null capture return
//        verify(dao).insertContributors(inserted.capture() ?: emptyList())


        MatcherAssert.assertThat(inserted.value.size, CoreMatchers.`is`(1))
    }
}