package kim.jeonghyeon.androidlibrary.architecture.net.model

import androidx.annotation.Keep

@Keep
//todo response body and error body is different. sever will devliver it different way.
data class ErrorBody(
        val status: Int,
        val error: String,
        val message: String,
        val path: String,
        val timestamp: String
)