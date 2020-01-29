package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.getSavedState
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

/**
 * A placeholder fragment containing a simple view.
 */
class SampleBaseFragment : BaseFragment() {

    val viewModel: SampleMVVMViewModel by bindingViewModel {
        parametersOf(getSavedState())
    }

    override val layoutId: Int
        get() = R.layout.fragment_mvvm

//    override fun setVariable(binding: FragmentMvvmBinding) {
//        //set your variable
//        binding.model = viewModel
//    }

}