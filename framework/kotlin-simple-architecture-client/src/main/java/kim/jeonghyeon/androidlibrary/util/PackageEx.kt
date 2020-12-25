package kim.jeonghyeon.androidlibrary.util

import android.content.pm.PackageManager
import kim.jeonghyeon.androidlibrary.extension.ctx

val PackageManager.packageName: String
    get() = getApplicationLabel(ctx.applicationInfo).toString()

val PackageManager.installTime: Long
    get() = getPackageInfo(packageName, 0).firstInstallTime