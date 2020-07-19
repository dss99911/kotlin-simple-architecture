package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiParallelViewModel

class ApiParallelViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiParallelViewModel()

    val list by cflow { viewModel.list }
    val input1 by cflow { viewModel.input1 }
    val input2 by cflow { viewModel.input2 }
    val input3 by cflow { viewModel.input3 }

    fun onClick() {
        viewModel.onClick()
    }
}