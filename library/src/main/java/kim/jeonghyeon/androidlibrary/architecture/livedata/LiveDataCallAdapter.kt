package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kim.jeonghyeon.androidlibrary.architecture.net.error.*
import kim.jeonghyeon.androidlibrary.architecture.net.model.BaseResponseBody
import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

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
class LiveDataCallAdapter<R, T : BaseResponseBody<R>>(private val responseType: Type) :
    CallAdapter<T, LiveData<Resource<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<T>): LiveData<Resource<R>> {
        val result : MutableLiveData<Resource<R>> = MutableLiveData(Resource.Loading)
        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                val error = when (t) {
                    is IOException -> NoNetworkError(t)
                    else -> UnknownError(t)
                }
                result.postError(error)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val resource = if (response.isSuccessful && body != null) {
                    if (body.isSuccess()) {
                        Resource.Success(body.data)
                    } else {
                        Resource.Error(MessageCodeError(body.code, body.message?:""))
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    if (!errorBodyString.isNullOrEmpty()) {
                        try {
                            val errorBody = Gson().fromJson(errorBodyString, ErrorBody::class.java)
                            Resource.Error(ErrorBodyError(errorBody))
                        } catch (e: JsonSyntaxException) {
                            e.printStackTrace()
                            getHttpError(response)
                        }
                    } else {
                        getHttpError(response)
                    }
                }
                result.postValue(resource)
            }

            private fun getHttpError(response: Response<T>): Resource.Error {
                val msg = response.message()

                return if (msg.isNullOrEmpty()) {
                    Resource.Error(UnknownError())
                } else {
                    Resource.Error(HttpError(response.code(), msg))
                }
            }
        })
        return result
    }
}
