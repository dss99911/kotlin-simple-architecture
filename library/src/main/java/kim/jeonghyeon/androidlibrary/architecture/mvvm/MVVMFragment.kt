package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment
import kim.jeonghyeon.androidlibrary.extension.createProgressDialog
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showWithoutException
import kim.jeonghyeon.androidlibrary.extension.toast
import org.jetbrains.anko.support.v4.toast

/**
 * Methods
 * - setMenu()
 */

interface IMvvmFragment<VM : BaseViewModel, DB : ViewDataBinding> {
    /**
     * viewModel name should be "model" for auto binding
     * if you'd like to change it, override setVariable
     */
    val viewModel: VM
    val layoutId: Int

    fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean)

    fun setVariable(binding: DB)
}

abstract class MVVMFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment(),
    IMvvmFragment<VM, DB> {
    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean
    //todo when call?
    override fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean) {
        this.menuId = menuId
        this.onMenuItemClickListener = onMenuItemClickListener
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!::onMenuItemClickListener.isInitialized) {
            return super.onOptionsItemSelected(item)
        }

        return onMenuItemClickListener(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(menuId, menu)
    }

    override fun setVariable(binding: DB) {
        binding.setVariable(BR.model, viewModel)
    }

    private val progressDialog by lazy { activity?.createProgressDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {
            toast.observeEvent(this@MVVMFragment) {
                toast(it)
            }

            startActivity.observeEvent(this@MVVMFragment) {
                activity?.startActivity(it)
            }

            startActivityForResult.observeEvent(this@MVVMFragment) { (intent, requestCode) ->
                try {
                    startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            showProgressBar.observeEvent(this@MVVMFragment) {
                if (it) {
                    progressDialog?.showWithoutException()
                } else {
                    progressDialog?.dismissWithoutException()
                }
            }

            addFragment.observeEvent(this@MVVMFragment) {
                this@MVVMFragment.addFragment(it.containerId, it.fragment, it.tag)
            }

            replaceFragment.observeEvent(this@MVVMFragment) {
                this@MVVMFragment.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            performWithActivity.observeEvent(this@MVVMFragment) {
                it(requireActivity())
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
        binding.lifecycleOwner = this
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