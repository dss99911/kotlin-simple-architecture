package kim.jeonghyeon.client

import kim.jeonghyeon.type.CFlow

//todo why is this not recognized on ios?
open class EmptyViewModelIos : BaseViewModelIos() {
    override val viewModel: BaseViewModel
        get() = BaseViewModel()
    override val flows: Array<CFlow<*>>
        get() = arrayOf()
}