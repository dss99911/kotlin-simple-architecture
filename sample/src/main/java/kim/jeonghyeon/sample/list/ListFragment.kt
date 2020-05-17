package kim.jeonghyeon.sample.list

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ListFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_list

    val viewModel: ListViewModel by bindingViewModel()

}