package kim.jeonghyeon.sample.view.parcelable

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.jvm.extension.toJsonString

class ParcelableViewModel(val data: ParcelableData<TestOpion>) : BaseViewModel() {
    val dataString = data.toJsonString()

}
