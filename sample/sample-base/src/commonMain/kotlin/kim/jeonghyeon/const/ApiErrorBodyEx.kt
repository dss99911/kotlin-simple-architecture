package kim.jeonghyeon.const

import kim.jeonghyeon.net.error.ApiErrorBody

val ApiErrorBody.Companion.forTest get() = ApiErrorBody(2000, "forTest")