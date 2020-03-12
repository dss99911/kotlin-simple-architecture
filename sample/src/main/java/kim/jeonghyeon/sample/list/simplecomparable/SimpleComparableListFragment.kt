package kim.jeonghyeon.sample.list.simplecomparable

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class SimpleComparableListFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_comparable_simple_list

    val viewModel: SimpleComparableListViewModel by bindingViewModel()
}