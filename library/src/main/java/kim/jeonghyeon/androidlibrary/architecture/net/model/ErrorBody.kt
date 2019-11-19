package kim.jeonghyeon.androidlibrary.architecture.net.model

import androidx.annotation.Keep

@Keep
//todo is it system error? check the exact use case
data class ErrorBody(
        val status: Int,
        val error: String,
        val message: String,
        val path: String,
        val timestamp: String
)