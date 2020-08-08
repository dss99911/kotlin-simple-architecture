package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiAnnotationViewModel
import kim.jeonghyeon.sample.viewmodel.ApiExternalViewModel
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel

//todo this can be removed from 1.4
class ApiExternalViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiExternalViewModel()

    val repoList by cflow { viewModel.repoList }

    val input by cflow { viewModel.input }

    fun onClickCall() {
        viewModel.onClickCall()
    }
}