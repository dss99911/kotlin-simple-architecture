package kim.jeonghyeon.sample.list.radiobox

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class RadioBoxListViewModel : BaseViewModel() {
    val checkedPosition = LiveObject(0)
    val sampleList = LiveObject<List<RadioBoxListItemViewModel>>().apply {
        value = (1..10).mapIndexed { index, number ->
            RadioBoxListItemViewModel(index, number.toString(), checkedPosition)
        }
    }
}

class RadioBoxListItemViewModel(
    val index: Int,
    val number: String,
    val checkedPosition: LiveObject<Int>
) {
    fun onRadioChecked() {
        checkedPosition.value = index
    }
}