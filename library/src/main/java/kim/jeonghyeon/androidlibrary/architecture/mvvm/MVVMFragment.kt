package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment
import kim.jeonghyeon.androidlibrary.extension.createProgressDialog
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showWithoutException
import kim.jeonghyeon.androidlibrary.extension.toast

abstract class MVVMFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment() {
    abstract val viewModel: VM
    abstract val layoutId: Int

    open fun setVariable(binding: DB) {
        binding.setVariable(BR.model, viewModel)
    }

    private val progressDialog by lazy { activity?.createProgressDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {
            toast.observe(this@MVVMFragment) {
                toast(it)
            }

            startActivity.observe(this@MVVMFragment) {
                activity?.startActivity(it)
            }

            startActivityForResult.observe(this@MVVMFragment) { (intent, requestCode) ->
                try {
                    startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            showProgressBar.observe(this@MVVMFragment) {
                if (it) {
                    progressDialog?.showWithoutException()
                } else {
                    progressDialog?.dismissWithoutException()
                }
            }

            addFragment.observe(this@MVVMFragment) {
                this@MVVMFragment.addFragment(it.containerId, it.fragment, it.tag)
            }

            replaceFragment.observe(this@MVVMFragment) {
                this@MVVMFragment.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            performWithActivity.observe(this@MVVMFragment) {
                //todo as fragment is active, this is processed. but it seems not to ensure activity exists. but let's try if this crash or not
                it(activity!!)
            }

            onCreate()
        }

    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<DB>(inflater, layoutId, container, false)
        setVariable(binding)
        binding.setLifecycleOwner(this)
        return binding.root
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