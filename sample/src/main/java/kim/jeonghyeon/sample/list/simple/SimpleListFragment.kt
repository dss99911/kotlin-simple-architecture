package kim.jeonghyeon.sample.list.simple

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class SimpleListFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_simple_list

    val viewModel: SimpleListViewModel by bindingViewModel()
}