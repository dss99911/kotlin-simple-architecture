package kim.jeonghyeon.androidlibrary.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.R

fun View.showSnackbar(snackbarText: String, timeLength: Int): Snackbar =
    Snackbar.make(this, snackbarText, timeLength).also { it.show() }

fun View.showSnackbar(snackbarText: String, timeLength: Int, retry: () -> Unit): Snackbar =
    //TODO if Snackbar is shown, FragmentScenario.launchInContainer loading unlimited.
    Snackbar.make(
        this,
        snackbarText,
        if (isTesting) Snackbar.LENGTH_SHORT else timeLength
    ).setAction(R.string.retry) {
        retry()
    }.also { it.show() }