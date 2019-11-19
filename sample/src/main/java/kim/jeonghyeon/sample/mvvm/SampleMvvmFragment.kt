package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.extension.simpleViewModels
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMvvmBinding

/**
 * A placeholder fragment containing a simple view.
 */
class SampleMvvmFragment : MvvmFragment<SampleMVVMViewModel, FragmentMvvmBinding>() {

    override val viewModel: SampleMVVMViewModel by simpleViewModels {
        SampleMVVMViewModel(getSavedState())
    }

    override val layoutId: Int
        get() = R.layout.fragment_mvvm

//    override fun setVariable(binding: FragmentMvvmBinding) {
//        //set your variable
//        binding.model = viewModel
//    }

}