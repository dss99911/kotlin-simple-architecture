package kim.jeonghyeon.sample.apicall.threading

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceException
import kim.jeonghyeon.androidlibrary.architecture.net.error.ErrorBodyError
import kim.jeonghyeon.androidlibrary.architecture.net.error.HttpError
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody
import kim.jeonghyeon.androidlibrary.architecture.net.model.ResponseCodeConstants.ERROR_CUSTOM
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.lang.reflect.Type

class ThreadingCallAdapter<U>(
    private val type: Type
) : CallAdapter<U, U> {
    override fun responseType() = type
    override fun adapt(call: Call<U>): U {
        return getReturn(call.execute())
    }

    private fun getReturn(response: Response<U>): U {
        val body = response.body()
        if (response.isSuccessful) {
            return body as U
        } else {
            val errorBodyString = response.errorBody()?.string()
            if (response.code() == ERROR_CUSTOM && !errorBodyString.isNullOrEmpty()) {
                try {
                    ErrorBodyError(Gson().fromJson(errorBodyString, ErrorBody::class.java))
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    getHttpError(response)
                }
            } else {
                getHttpError(response)
            }.let {
                throw ResourceException(it)
            }
        }
    }

    private fun getHttpError(response: Response<U>): ResourceError {
        val msg = response.message()

        return if (msg.isNullOrEmpty()) {
            UnknownResourceError()
        } else {
            HttpError(response.code(), msg)
        }
    }
}
