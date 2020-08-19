package kim.jeonghyeon.const

import kim.jeonghyeon.net.error.ApiErrorBody

val ApiErrorBody.Companion.post get() = ApiErrorBody(1001, "post error")