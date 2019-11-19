package kim.jeonghyeon.androidlibrary.architecture.net.error

import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody

class ErrorBodyError(val errorBody: ErrorBody) : HttpError(errorBody.status, errorBody.message)