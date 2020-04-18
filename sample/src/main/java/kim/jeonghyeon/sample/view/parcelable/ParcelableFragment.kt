package kim.jeonghyeon.sample.view.parcelable

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.extension.getNavArgs
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

class ParcelableFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_parcelable

    val viewModel: ParcelableViewModel by bindingViewModel {
        parametersOf(getNavArgs<ParcelableFragmentArgs>().data)
    }

}