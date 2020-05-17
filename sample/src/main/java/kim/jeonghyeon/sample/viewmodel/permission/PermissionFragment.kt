package kim.jeonghyeon.sample.viewmodel.permission

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class PermissionFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_permission
    val viewModel: PermissionViewModel by bindingViewModel()

}