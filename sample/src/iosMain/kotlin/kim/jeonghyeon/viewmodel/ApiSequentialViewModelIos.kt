package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiSequentialViewModel

class ApiSequentialViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiSequentialViewModel()

    val list by cflow { viewModel.list }
    val textList by cflow { viewModel.textList }
    val input1 by cflow { viewModel.input1 }
    val input2 by cflow { viewModel.input2 }
    val input3 by cflow { viewModel.input3 }

    fun onClick() {
        viewModel.onClick()
    }


}