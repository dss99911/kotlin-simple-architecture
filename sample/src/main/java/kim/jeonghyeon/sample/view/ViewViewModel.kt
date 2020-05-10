package kim.jeonghyeon.sample.view

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.sample.view.parcelable.ParcelableData
import kim.jeonghyeon.sample.view.parcelable.TestOpion

class ViewViewModel : BaseViewModel() {
    fun onClickParcelable() {
        val data = ParcelableData.create(
            1,
            { toast("dd") },
            TestOpion(true, "test")
        )

        ViewFragmentDirections.actionViewFragmentToParcelableFragment(data).navigate()
    }
}

