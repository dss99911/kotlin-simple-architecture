package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.ApiDbViewModel

class ApiDbViewModelIos : BaseViewModelIos() {
    override val viewModel = ApiDbViewModel()

    val wordList by cflow { viewModel.wordList }
    val newWord by cflow { viewModel.newWord }

    fun onClickAdd() {
        viewModel.onClickAdd()
    }
}