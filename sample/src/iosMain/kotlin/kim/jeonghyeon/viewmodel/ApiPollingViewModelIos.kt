package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiPollingViewModel

class ApiPollingViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiPollingViewModel()

    val result by cflow { viewModel.result }
    val count by cflow { viewModel.count }
}