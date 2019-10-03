package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseActivity
import kim.jeonghyeon.androidlibrary.extension.createProgressDialog
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showWithoutException
import kim.jeonghyeon.androidlibrary.extension.toast

abstract class MVVMActivity<VM : BaseViewModel> : BaseActivity() {

    abstract val viewModel: VM
    private val progressDialog by lazy { createProgressDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {
            toast.observeEvent(this@MVVMActivity) {
                toast(it)
            }

            startActivity.observeEvent(this@MVVMActivity) {
                startActivity(it)
            }

            startActivityForResult.observeEvent(this@MVVMActivity) { (intent, requestCode) ->
                try {
                    startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            showProgressBar.observeEvent(this@MVVMActivity) {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }

            addFragment.observeEvent(this@MVVMActivity) {
                this@MVVMActivity.addFragment(it.containerId, it.fragment, it.tag)
            }

            replaceFragment.observeEvent(this@MVVMActivity) {
                this@MVVMActivity.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            performWithActivity.observeEvent(this@MVVMActivity) {
                it(this@MVVMActivity)
            }

            onCreate()
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }
}