package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.architecture.net.error.MessageError
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showSnackbar
import kim.jeonghyeon.androidlibrary.extension.showWithoutException

fun IBaseUi.resourceObserverCommon(onResult: (State) -> Boolean = { false }): Observer<State> =
    Observer {
        if (onResult(it)) {
            return@Observer
        }
        if (it.isLoading()) {
            progressDialog.showWithoutException()
        } else {
            progressDialog.dismissWithoutException()
        }

        dismissSnackbar(binding.root)// if state is changed, dismiss snackbar if it's shown.

        it.onError {
            val errorMessage = if (it.error is MessageError) it.error.errorMessage else null
            showErrorSnackbar(binding.root, errorMessage, it.retry)
        }

    }

fun IBaseUi.resourceObserverInit(onResult: (State) -> Boolean = { false }): Observer<State> =
    resourceObserverCommon {
        if (onResult(it)) {
            return@resourceObserverCommon true
        }

        binding.root.visibility = if (it.isSuccess()) View.VISIBLE else View.GONE
        false
    }

fun dismissSnackbar(view: View) {
    val snackbar = view.getTag(R.id.view_tag_snackbar) as? Snackbar? ?: return
    snackbar.dismiss()
    view.setTag(R.id.view_tag_snackbar, null)
}

fun showErrorSnackbar(view: View, message: String? = null, retry: () -> Unit) {
    val snackbarText =
        ctx.getString(R.string.error_occurred) + if (message == null) "" else " : $message"
    view.showSnackbar(snackbarText, Snackbar.LENGTH_INDEFINITE, retry).also {
        view.setTag(R.id.view_tag_snackbar, it)
    }
}