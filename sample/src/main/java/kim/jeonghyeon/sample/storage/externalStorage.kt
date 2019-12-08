package kim.jeonghyeon.sample.storage

import android.os.Environment

fun checkExternalStorageAvailable() {
    Environment.getExternalStorageState()
}