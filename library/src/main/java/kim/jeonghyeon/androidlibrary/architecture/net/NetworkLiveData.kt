package kim.jeonghyeon.androidlibrary.architecture.net

import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource

abstract class NetworkLiveData<DATA> : LiveData<Resource<DATA>>() {

//    @Suppress("LeakingThis")
//    val asLiveData: LiveData<Resource<DATA>> = this
//
//    private val isFirst = AtomicBoolean(true)
//
//    init {
//        value = Resource.loading(null)
//    }
//
//    override fun onActive() {
//        super.onActive()
//        if (isFirst.getAndSet(false)) {
//            fetchFromNetwork()
//        }
//    }
//
//    private fun fetchFromNetwork() {
//        val call = createCall()
//
//        call.call(object : UPIResponseListener<DATA>() {
//            override fun onSuccess(response: UPIResponse<DATA>) {
//                postValue(Resource.loaded(response.data))
//            }
//
//            override fun onFail(response: UPIResponse<DATA>?) {
//                Resource.error(
//                        response?.data?.code?.toString() ?: UPIResponseCodes.RESPONSE_UNKNOWN_FAIL,
//                        response?.data?.description ?: response?.resultMessage,
//                        response?.data
//                ).let { postValue(it) }
//
//            }
//
//            override fun onError(throwable: Throwable?) {
//                postValue(Resource.error(convertErrorToErrorCode(throwable), null, null))
//            }
//
//            override fun showAlert(alert: Alert) {
//                postValue(Resource.alert(alert, null))
//            }
//        })
//    }
//
//    protected abstract fun createCall(): Call<UPIResponse<DATA>>
//
//    fun convertErrorToErrorCode(throwable: Throwable?): String = when (throwable) {
//        is NetworkNotConnectedException -> {
//            UPIResponseCodes.RESPONSE_NO_CONNECTION
//        }
//        is UnknownHostException -> {
//            UPIResponseCodes.RESPONSE_TIMEOUT
//        }
//        else -> {
//            UPIResponseCodes.RESPONSE_UNKNOWN_ERROR
//        }
//    }

}