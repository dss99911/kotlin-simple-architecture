package kim.jeonghyeon.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.BaseViewModelIos
import kim.jeonghyeon.type.CFlow

//todo move to library. but xcode doesn't recognize it.
open class EmptyViewModelIos : BaseViewModelIos() {
    override val viewModel: BaseViewModel
        get() = BaseViewModel()
    override val flows: Array<CFlow<*>>
        get() = arrayOf()
}