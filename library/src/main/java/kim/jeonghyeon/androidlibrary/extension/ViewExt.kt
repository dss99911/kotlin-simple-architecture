package kim.jeonghyeon.androidlibrary.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.R

fun View.showSnackbar(snackbarText: String, timeLength: Int): Snackbar =
    Snackbar.make(this, snackbarText, timeLength).also { it.show() }

fun View.showSnackbar(snackbarText: String, timeLength: Int, retry: () -> Unit): Snackbar =
    Snackbar.make(this, snackbarText, timeLength).setAction(R.string.retry) {
        retry()
    }.also { it.show() }