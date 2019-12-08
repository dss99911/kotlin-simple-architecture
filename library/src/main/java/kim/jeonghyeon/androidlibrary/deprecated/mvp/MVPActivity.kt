package kim.jeonghyeon.androidlibrary.deprecated.mvp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kim.jeonghyeon.androidlibrary.architecture.BaseActivity

abstract class MVPActivity<P : Presenter<U>, U : Ui> : BaseActivity(), Ui {
    private lateinit var presenter: P

    protected abstract val ui: U

    protected abstract fun createPresenter(): P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.onUiReady(ui)
        if (savedInstanceState != null) {
            presenter.onRestoreInstanceState(savedInstanceState)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, requestCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onUiUnready(ui)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override val uiContext: Context
        get() = this

    override val baseActivity: MVPActivity<*, *>?
        get() = this

    override fun getActivity(): FragmentActivity? {
        return this
    }
}
