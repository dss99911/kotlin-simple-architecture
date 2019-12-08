package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showSnackbar
import kim.jeonghyeon.androidlibrary.extension.showWithoutException

fun <T> MvvmActivity<*, *>.resourceObserverCommon(onSuccess: (T) -> Unit): Observer<Resource<T>> =
    Observer {
        if (it.isLoading()) {
            progressDialog.showWithoutException()
        } else {
            progressDialog.dismissWithoutException()
        }

        it.onError {
            binding.root.showSnackbar("error occurred", Snackbar.LENGTH_SHORT)
        }

        it.onSuccess(onSuccess)
    }

fun <T> MvvmFragment<*, *>.resourceObserverCommon(onSuccess: (T) -> Unit): Observer<Resource<T>> =
    Observer {
        if (it.isLoading()) {
            progressDialog?.showWithoutException()
        } else {
            progressDialog?.dismissWithoutException()
        }

        it.onError {
            binding.root.showSnackbar("error occurred", Snackbar.LENGTH_SHORT)
        }

        it.onSuccess(onSuccess)
    }