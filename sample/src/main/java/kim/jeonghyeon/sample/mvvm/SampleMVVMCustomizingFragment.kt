package kim.jeonghyeon.sample.mvvm

import android.os.Bundle
import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMvvmBinding

/**
 * A placeholder fragment containing a simple view.
 */
class SampleMVVMCustomizingFragment : MVVMFragment<SampleViewModel, FragmentMvvmBinding>() {
    override val viewModel: SampleViewModel by viewModels()

    override val layoutId: Int
        get() = R.layout.fragment_mvvm

    override fun setVariable(binding: FragmentMvvmBinding) {
        //todo set your variable
        binding.model = viewModel
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): SampleMVVMCustomizingFragment {
            return SampleMVVMCustomizingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}