package kim.jeonghyeon.sample.list.paging

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class PagingFragment : BaseFragment() {
    val viewModel: PagingViewModel by bindingViewModel()


    override val layoutId = R.layout.fragment_paging

}