package kim.jeonghyeon.sample.etc.storage

import android.os.Environment

fun checkExternalStorageAvailable() {
    Environment.getExternalStorageState()
}