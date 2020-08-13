package kim.jeonghyeon.const

import kim.jeonghyeon.net.error.ApiErrorBody

val ApiErrorBody.Companion.post get() = ApiErrorBody(1001, "post error")
val ApiErrorBody.Companion.credentialInvalid get() = ApiErrorBody(1002, "ID or Password incorrect")
val ApiErrorBody.Companion.idAlreadyExists get() = ApiErrorBody(1003, "ID exists")