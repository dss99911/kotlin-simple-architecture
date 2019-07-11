package kim.jeonghyeon.androidlibrary.sample.test.api

import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.testutil.BaseApiTest
import kim.jeonghyeon.androidlibrary.testutil.ObjectComparator
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.http.GET

@RunWith(JUnit4::class)
class HomeServiceTest : BaseApiTest<HomeApi>(){
    override val apiClass: Class<HomeApi>
        get() = HomeApi::class.java

    @Test
    fun test() {
        val actual = callAndResponse(service.test(), "home.json")
        val expected = Home("my home", "100", "200")

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/test2"))


        ObjectComparator.compare(Resource.success(expected), actual)
    }

}

data class Home(val name: String, val price: String, val size: String)

interface HomeApi {
    @GET("test2")
    fun test(): LiveData<Resource<Home>>
}