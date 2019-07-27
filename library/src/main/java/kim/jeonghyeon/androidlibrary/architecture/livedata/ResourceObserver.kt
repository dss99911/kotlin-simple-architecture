package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.Observer
import kim.jeonghyeon.androidlibrary.architecture.net.error.BaseError

open class ResourceObserver<T>(private val onResult: Resource<T>.() -> Unit = {}) : Observer<Resource<T>> {
    override fun onChanged(t: Resource<T>?) {
        when (t.status) {
            ResourceStatus.SUCCESS -> onSuccess(t!!.data)
            ResourceStatus.ERROR -> onError(t!!.state.error!!)
            ResourceStatus.LOADING -> onLoading()
        }
        onResult(t ?: return)
    }

    open fun onLoading() {

    }

    open fun onSuccess(data: T?) {

    }

    open fun onError(error: BaseError) {

    }
}
