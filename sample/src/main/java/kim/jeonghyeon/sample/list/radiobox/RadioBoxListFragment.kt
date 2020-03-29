package kim.jeonghyeon.sample.list.radiobox

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.extension.bindData
import kim.jeonghyeon.sample.R
import kotlinx.android.synthetic.main.fragment_radio_box_list.*

class RadioBoxListFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_radio_box_list
    val viewModel: RadioBoxListViewModel by bindingViewModel()


    override fun onViewModelSetup() {
        viewModel.sampleList {
            recycler_view.bindData(it, R.layout.item_radio_number, viewLifecycleOwner)
        }
    }
}