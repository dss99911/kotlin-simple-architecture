package kim.jeonghyeon.androidlibrary.architecture.net.error

class NeedRetryError(val retry: () -> Unit) : BaseError()