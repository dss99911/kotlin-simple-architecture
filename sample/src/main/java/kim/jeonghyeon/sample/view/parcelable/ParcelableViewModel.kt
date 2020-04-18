package kim.jeonghyeon.sample.view.parcelable

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.kotlinlibrary.extension.toJsonString

class ParcelableViewModel(val data: ParcelableData<TestOpion>) : BaseViewModel() {
    val dataString = data.toJsonString()

}
