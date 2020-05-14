package kim.jeonghyeon.sample.db.simple

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel

class SimpleDbFragment : BaseFragment() {
    override val layoutId: Int = kim.jeonghyeon.sample.R.layout.fragment_simple_db
    val viewModel: SimpleDbViewModel by bindingViewModel()
}