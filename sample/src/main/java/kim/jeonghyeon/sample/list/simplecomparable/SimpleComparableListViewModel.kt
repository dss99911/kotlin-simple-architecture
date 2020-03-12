package kim.jeonghyeon.sample.list.simplecomparable

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

class SimpleComparableListViewModel : BaseViewModel() {
    val sampleList = (1..10).map { SimpleComparableListItemViewModel(it.toString()) }
}

class SimpleComparableListItemViewModel(val number: String) :
    DiffComparable<SimpleComparableListItemViewModel> {
    override fun areItemsTheSame(item: SimpleComparableListItemViewModel): Boolean =
        number == item.number

    override fun areContentsTheSame(item: SimpleComparableListItemViewModel): Boolean =
        number == item.number

    fun onClick() {
        toast(number)
    }
}