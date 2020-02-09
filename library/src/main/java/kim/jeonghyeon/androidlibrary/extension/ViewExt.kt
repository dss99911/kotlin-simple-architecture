package kim.jeonghyeon.androidlibrary.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    log("Show snackbar")
    Snackbar.make(this, snackbarText, timeLength).show()
}