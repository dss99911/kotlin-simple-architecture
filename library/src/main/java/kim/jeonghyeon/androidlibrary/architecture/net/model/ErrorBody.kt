package kim.jeonghyeon.androidlibrary.architecture.net.model

import androidx.annotation.Keep

//response body and error body is different. sever will devliver it different way.
@Keep
data class ErrorBody(
    val code: Int,
    val message: String
)