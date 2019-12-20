package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.observe
import androidx.navigation.NavArgs
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
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

    /**
     * toolbar id is "toolbar"
     * If you want to change, override this property
     */
    val toolbarId: Int
    val appBarConfiguration: AppBarConfiguration?

    fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean)

    fun navigate(@IdRes id : Int)
    fun navigate(navDirections: NavDirections)

    /**
     * when observe LiveData, override this
     */
    fun VM.onViewModelSetup()

//  fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle
//fun <reified T : NavArgs> getNavArgs(): T

    /**
     * set state observer to change loading and error on state liveData
     */
    var stateObserver: Observer<ResourceState>
}

abstract class MvvmFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment(),
    IMvvmFragment<VM, DB> {
    override lateinit var binding: DB
    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean
    internal val progressDialog by lazy { activity?.createProgressDialog() }

    override val toolbarId: Int
        get() = R.id.toolbar

    override val appBarConfiguration: AppBarConfiguration?
        get() = AppBarConfiguration(findNavController().graph)

    override var stateObserver: Observer<ResourceState> = resourceObserverCommon {  }
        set(value) {
            viewModel.state.removeObserver(field)
            field = value
            viewModel.state.observe(this@MvvmFragment, value)
        }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        setVariable(binding)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupActionbar()
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
        if (menuId == 0) {
            return super.onCreateOptionsMenu(menu, inflater)
        }
        inflater.inflate(menuId, menu)
    }

    override fun setVariable(binding: DB) {
        binding.setVariable(BR.model, viewModel)
    }

    private fun setupObserver() {
        with(viewModel) {
            state.observe(this@MvvmFragment, stateObserver)

            eventToast.observeEvent(this@MvvmFragment) {
                toast(it)
            }

            eventSnackbar.observeEvent(this@MvvmFragment) {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            eventStartActivity.observeEvent(this@MvvmFragment) {
                startActivity(it)
            }

            eventStartActivityForResult.observeEvent(this@MvvmFragment) { (requestCode, intent) ->
                try {
                    this@MvvmFragment.startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            eventShowProgressBar.observeEvent(this@MvvmFragment) {
                if (it) {
                    progressDialog?.showWithoutException()
                } else {
                    progressDialog?.dismissWithoutException()
                }
            }

            eventAddFragment.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.addFragment(it.containerId, it.fragment, it.tag)
            }

            eventNavDirectionId.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.navigate(it)
            }

            eventNav.observeEvent(this@MvvmFragment) {action ->
                action(findNavController())
            }

            eventNavDirection.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.navigate(it)
            }

            eventReplaceFragment.observeEvent(this@MvvmFragment) {
                this@MvvmFragment.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            eventPerformWithActivity.observe(this@MvvmFragment) { array ->
                array.forEach { event ->
                    if (!event.hasBeenHandled) {
                        event.popContent()(requireActivity())
                    }
                }

            }

            lifecycle.addObserver(this)

            onViewModelSetup()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupActionbar() {
        val toolbar = binding.root.findViewById<View>(toolbarId)
        if (toolbar == null || toolbar !is Toolbar) {
            return
        }

        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        //todo check if it's working
        if (toolbar.menu != null) {
            setHasOptionsMenu(true)
        }

        //todo check if it's working
        appBarConfiguration?.let {
            //the title in the action bar will automatically be updated when the destination changes
            // [AppBarConfiguration] you provide controls how the Navigation button is displayed.
            setupActionBarWithNavController(activity, findNavController(), it)
        }


    }

    override fun VM.onViewModelSetup() {

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

    @Suppress("UNCHECKED_CAST")
    fun <A : BaseViewModel> getActivityViewModel(): A {
        return (requireActivity() as MvvmActivity<*,*>).viewModel as A
    }

    inline fun <reified T : NavArgs> getNavArgs(): T = navArgs<T>().value
}