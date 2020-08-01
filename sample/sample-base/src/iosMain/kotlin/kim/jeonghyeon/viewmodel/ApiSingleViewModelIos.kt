package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel

//todo this can be removed from 1.4
class ApiSingleViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiSingleViewModel()

    val result by cflow { viewModel.result }

    val input by cflow { viewModel.input }

    fun onClick() {
        viewModel.onClick()
    }
}