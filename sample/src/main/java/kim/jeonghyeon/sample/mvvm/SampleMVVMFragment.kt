package kim.jeonghyeon.sample.mvvm

import android.os.Bundle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.androidlibrary.extension.getViewModel
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMvvmBinding

/**
 * A placeholder fragment containing a simple view.
 */
class SampleMVVMFragment : MVVMFragment<SampleViewModel, FragmentMvvmBinding>() {
    override val viewModel: SampleViewModel
        get() = getViewModel()

    override val layoutId: Int
        get() = R.layout.fragment_mvvm

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
        fun newInstance(sectionNumber: Int): SampleMVVMFragment {
            return SampleMVVMFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}