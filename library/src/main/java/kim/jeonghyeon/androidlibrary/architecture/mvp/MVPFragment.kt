@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.architecture.mvp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment

@SuppressWarnings("unused")
abstract class MVPFragment<P : Presenter<U>, U : Ui> : BaseFragment(), Ui {

    private lateinit var presenter: P

    protected abstract val ui: U

    protected abstract fun createPresenter(): P

    override val baseActivity: MVPActivity<*, *>?
        get() = activity as MVPActivity<*, *>?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
        if (savedInstanceState != null) {
            presenter.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onUiReady(ui)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, requestCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onUiUnready(ui)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override val uiContext: Context?
        get() = context

    override fun finish() {
        activity?.finish()
    }

    override fun requestPermissions(listener: PermissionResultListener, vararg permissions: String) {
        baseActivity?.requestPermissions(listener, *permissions)
    }
}
