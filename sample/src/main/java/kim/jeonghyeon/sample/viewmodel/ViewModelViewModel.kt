package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class ViewModelViewModel : BaseViewModel() {
    fun onClickNavAgrs() {
        ViewModelFragmentDirections.actionViewModelFragmentToNavArgsFragment().apply {
            abc = 2
        }.let { navigateDirection(it) }
    }
}
