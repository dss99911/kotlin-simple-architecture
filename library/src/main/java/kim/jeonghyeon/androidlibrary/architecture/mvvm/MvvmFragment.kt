package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.NavArgs
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment
import kim.jeonghyeon.androidlibrary.extension.*
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
    fun setVariable(binding: DB)
    var binding: DB

    val layoutId: Int

    fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean)



    fun navigate(@IdRes id : Int)
    fun navigate(navDirections: NavDirections)

//  fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle
//fun <reified T : NavArgs> getNavArgs(): T
}

abstract class MvvmFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment(),
    IMvvmFragment<VM, DB> {
    override lateinit var binding: DB
    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean
    private val progressDialog by lazy { activity?.createProgressDialog() }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        setVariable(binding)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
    }

    override fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean) {
        this.menuId = menuId
        this.onMenuItemClickListener = onMenuItemClickListener
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!::onMenuItemClickListener.isInitialized) {
            return super.onOptionsItemSelected(item)
        }

        //if menu id and nav's fragment id is same, then redirect
        if (item.onNavDestinationSelected(findNavController())) {
            return true
        }

        return onMenuItemClickListener(item)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(menuId, menu)
    }

    override fun setVariable(binding: DB) {
        binding.setVariable(BR.model, viewModel)
    }

    private fun setupObserver() {
        with(viewModel) {
            toast.observeEvent(this@MvvmFragment) {
                toast(it)
            }

            snackbar.observeEvent(this@MvvmFragment) {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            startActivity.observeEvent(this@MvvmFragment) {
                activity?.startActivity(it)
            }

            startActivityForResult.observeEvent(this@MvvmFragment) { (intent, requestCode) ->
                try {
                    startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            showProgressBar.observeEvent(this@MvvmFragment) {
                if (it) {
                    progressDialog?.showWithoutException()
                } else {
                    progressDialog?.dismissWithoutException()
                }
            }

            addFragment.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.addFragment(it.containerId, it.fragment, it.tag)
            }

            navDirectionId.observeEvent(this@MvvmFragment) {
                navigate(it)
            }

            navDirection.observeEvent(this@MvvmFragment) {
                navigate(it)
            }

            replaceFragment.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            performWithActivity.observeEvent(this@MvvmFragment) {
                it(requireActivity())
            }

            lifecycle.addObserver(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    override fun navigate(id: Int) {
        findNavController().navigate(id)
    }

    override fun navigate(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle {
        return SavedStateViewModelFactory(
            app,
            savedStateRegistryOwner
        ).create(SavedStateViewModel::class.java)
            .savedStateHandle
    }

    fun <A : BaseViewModel> getActivityViewModel(): A {
        return (requireActivity() as MvvmActivity<*,*>).viewModel as A
    }

    inline fun <reified T : NavArgs> getNavArgs(): T = navArgs<T>().value
}