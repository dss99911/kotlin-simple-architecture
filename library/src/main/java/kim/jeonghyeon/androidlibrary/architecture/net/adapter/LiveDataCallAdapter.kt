package kim.jeonghyeon.androidlibrary.architecture.net.adapter

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseLiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.*
import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 *
 * error case
 * 1. no network -> UnknownHostException
 * 2. connection timeout(connect wifi which doesn't connect to internet)
 * -> UnknownHostException
 * 3. base url is wrong or server down -> UnknownHostException
 * 4. base url is correct but sub url is wrong -> 404 Not Found
 **/
class LiveDataCallAdapter<R>(private val responseType: Type) :
        CallAdapter<R, LiveData<Resource<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<Resource<R>> {
        return object : BaseLiveData<Resource<R>>() {
            override fun onFirstActive() {
                super.onFirstActive()

                call.enqueue(object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        val resource = if (response.isSuccessful) {
                            Resource.success(response.body())
                        } else {
                            val errorBodyString = response.errorBody()?.string()
                            if (!errorBodyString.isNullOrEmpty()) {
                                try {
                                    val errorBody = Gson().fromJson(errorBodyString, ErrorBody::class.java)
                                    Resource.error<R>(ErrorBodyError(errorBody))
                                } catch (e: JsonSyntaxException) {
                                    Resource.error<R>(UnknownError())
                                }
                            } else {
                                val msg = response.message()

                                if (msg.isNullOrEmpty()) {
                                    Resource.error(UnknownError())
                                } else {
                                    Resource.error(MessageError(msg))
                                }
                            }

                        }
                        postValue(resource)
                    }

                    override fun onFailure(call: Call<R>, throwable: Throwable) {
                        val error = when (throwable) {
                            is UnknownHostException -> NoNetworkError(throwable)
                            is SocketTimeoutException -> TimeoutError(throwable)
                            else -> UnknownError(throwable)
                        }
                        postValue(Resource.error(error))
                    }
                })
            }
        }
    }
}
