package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.sample.R

/**
 * A placeholder fragment containing a simple view.
 */
class SampleBaseFragment : BaseFragment() {

    val viewModel: SampleMVVMViewModel by addingViewModel {
        SampleMVVMViewModel(getSavedState())
    }

    override val layoutId: Int
        get() = R.layout.fragment_mvvm

//    override fun setVariable(binding: FragmentMvvmBinding) {
//        //set your variable
//        binding.model = viewModel
//    }

}