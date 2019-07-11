package kim.jeonghyeon.androidlibrary.sample.storage

import android.os.Environment

fun checkExternalStorageAvailable() {
    Environment.getExternalStorageState()
}