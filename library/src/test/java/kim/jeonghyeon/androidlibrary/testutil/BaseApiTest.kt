package kim.jeonghyeon.androidlibrary.testutil

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit

@RunWith(JUnit4::class)
abstract class BaseApiTest<T : Any> {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var service: T

    lateinit var mockWebServer: MockWebServer

    abstract val apiClass: Class<T>

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = testApi(mockWebServer.url("/").toString(), apiClass)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    protected fun <T> callAndResponse(call: LiveData<T>, responseFileName: String): T {
        enqueueResponse(responseFileName)
        return LiveDataTestUtil.getValue(call, 2, 2)
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
                mockResponse
                        .setBody(JsonUtil.getJsonFromFile(fileName))
        )
    }


}