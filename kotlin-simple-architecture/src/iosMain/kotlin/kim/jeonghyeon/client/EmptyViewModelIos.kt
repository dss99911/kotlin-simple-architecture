package kim.jeonghyeon.client

//todo why is this not recognized on ios?
open class EmptyViewModelIos : BaseViewModelIos() {
    override val viewModel: BaseViewModel = BaseViewModel()
}