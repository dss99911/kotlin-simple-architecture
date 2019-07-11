package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment
import kim.jeonghyeon.androidlibrary.extension.*
import java.lang.IllegalStateException

abstract class MVVMFragment<VM : BaseViewModel> : BaseFragment() {
    abstract val viewModel: VM
    private val progressDialog by lazy { activity?.createProgressDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.toast.observe(this) {
            toast(it)
        }

        viewModel.startActivity.observe(this) {
            activity?.startActivity(it)
        }

        viewModel.startActivityForResult.observe(this) { (intent, requestCode) ->
            try {
                startActivityForResult(intent, requestCode)
            } catch (e: IllegalStateException) {
            } catch (e: ActivityNotFoundException) {
                toast(R.string.toast_no_activity)
            }
        }

        viewModel.showProgressBar.observe(this) {
            if (it) {
                progressDialog?.showWithoutException()
            } else {
                progressDialog?.dismissWithoutException()
            }
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