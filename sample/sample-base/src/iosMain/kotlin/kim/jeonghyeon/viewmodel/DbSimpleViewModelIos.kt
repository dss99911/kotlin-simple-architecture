package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.sample.viewmodel.DbSimpleViewModel

class DbSimpleViewModelIos : BaseViewModelIos() {
    override val viewModel = DbSimpleViewModel()

    val wordList by cflow { viewModel.wordList }
    val newWord by cflow { viewModel.newWord }

    fun onClickAdd() {
        viewModel.onClickAdd()
    }
}