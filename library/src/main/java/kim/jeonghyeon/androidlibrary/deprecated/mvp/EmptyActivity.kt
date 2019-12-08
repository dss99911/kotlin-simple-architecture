package kim.jeonghyeon.androidlibrary.deprecated.mvp

open class EmptyActivity : MVPActivity<Presenter<Ui>, Ui>() {
    override val ui: Ui
        get() = this

    override fun createPresenter(): Presenter<Ui> = Presenter()
}