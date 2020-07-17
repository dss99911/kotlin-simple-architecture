package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel
import kim.jeonghyeon.type.CFlow

//todo this can be removed from 1.4
class ApiSingleViewModelIos : BaseViewModelIos() {
    override val viewModel by lazy { ApiSingleViewModel() }

    override val flows: Array<CFlow<*>> by lazy { arrayOf<CFlow<*>>(result, input) }

    val result by cflow { viewModel.result }

    val input by cflow { viewModel.input }

    fun onClick() {
        viewModel.onClick()
    }
}