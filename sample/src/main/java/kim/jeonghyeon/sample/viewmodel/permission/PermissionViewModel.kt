package kim.jeonghyeon.sample.viewmodel.permission

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.PermissionRequester
import kim.jeonghyeon.androidlibrary.architecture.mvvm.PermissionResultListener
import kim.jeonghyeon.androidlibrary.extension.toast

class PermissionViewModel : BaseViewModel() {

    fun onClickPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            object : PermissionResultListener {
                override fun onPermissionGranted() {
                    toast("permission granted")
                }

                override fun onPermissionDenied(deniedPermissions: Array<String>) {
                    onClickPermission()
                }

                override fun onPermissionDeniedPermanently(deniedPermissions: Array<String>) {
                    startPermissionSettingsPage {
                        onClickPermission()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(requester: PermissionRequester) {
                    showOkDialog("need permission. please allow") {
                        requester.request()
                    }
                }

                override fun onPermissionException() {

                }
            })
    }
}
