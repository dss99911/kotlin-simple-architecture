package kim.jeonghyeon.androidlibrary.sample.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.androidlibrary.extension.bind
import kim.jeonghyeon.androidlibrary.extension.getViewModel
import kim.jeonghyeon.androidlibrary.test.R

/**
 * A placeholder fragment containing a simple view.
 */
class SampleMVVMFragment : MVVMFragment<SampleViewModel>() {
    override val viewModel: SampleViewModel
        get() = getViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return bind<SampleMvvmFragmentBinding>(inflater, container, R.layout.sample_mvvm_fragment) {
            it.model = viewModel
        }.root
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
        fun newInstance(sectionNumber: Int): SampleMVVMFragment {
            return SampleMVVMFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}