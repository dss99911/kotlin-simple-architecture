package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.NonNull
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.observe
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseActivity
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.extension.*

interface IMvvmActivity<VM : BaseViewModel, DB : ViewDataBinding> {
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
    /**
     * if you want to use nav controller, override this
     */
    val navHostId: Int
    val navController: NavController
    fun navigate(@IdRes id : Int)
    fun navigate(navDirections: NavDirections)

    //has 'this' default parameter. so, commented out
    //fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle

    //has reified. so, commented out
    //fun <reified T : NavArgs> getNavArgs(): T

    /**
     * set state observer to change loading and error on state liveData
     */
    var stateObserver: Observer<ResourceState>
}

abstract class MvvmActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseActivity(), IMvvmActivity<VM, DB> {

    override val navHostId: Int = 0
    override val navController: NavController
        get() {
            require(navHostId != 0) { "navHostId is not set" }
            return findNavController(navHostId)
        }

    override val toolbarId: Int
        get() = R.id.toolbar

    override val appBarConfiguration: AppBarConfiguration?
        get() = if (navHostId != 0) AppBarConfiguration(navController.graph) else null


    internal val progressDialog by lazy { createProgressDialog() }

    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean




    override lateinit var binding: DB

    override var stateObserver: Observer<ResourceState> = resourceObserverCommon {  }
        set(value) {
            viewModel.state.removeObserver(field)
            field = value
            viewModel.state.observe(this@MvvmActivity, value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupActionbar()
        setupObserver()

    }

    private fun setupView() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        setVariable(binding)
        binding.lifecycleOwner = this
    }

    private fun setupObserver() {
        with(viewModel) {
            state.observe(this@MvvmActivity, stateObserver)

            eventToast.observeEvent(this@MvvmActivity) {
                toast(it)
            }

            eventSnackbar.observeEvent(this@MvvmActivity) {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            eventStartActivity.observeEvent(this@MvvmActivity) {
                startActivity(it)
            }

            eventStartActivityForResult.observeEvent(this@MvvmActivity) { (requestCode, intent) ->
                try {
                    this@MvvmActivity.startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            eventShowProgressBar.observeEvent(this@MvvmActivity) {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }

            eventAddFragment.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.addFragment(it.containerId, it.fragment, it.tag)
            }

            eventReplaceFragment.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            eventPerformWithActivity.observe(this@MvvmActivity) { array ->
                array.forEach { event ->
                    if (!event.hasBeenHandled) {
                        event.popContent()(this@MvvmActivity)
                    }
                }
            }

            eventNavDirectionId.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.navigate(it)
            }

            eventNav.observeEvent(this@MvvmActivity) {action ->
                action(navController)
            }

            eventNavDirection.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.navigate(it)
            }

            lifecycle.addObserver(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupActionbar() {
        val toolbar = findViewById<View>(toolbarId)
        if (toolbar == null || toolbar !is Toolbar) {
            return
        }

        setSupportActionBar(toolbar)

        appBarConfiguration?.let {
            //the title in the action bar will automatically be updated when the destination changes
            // [AppBarConfiguration] you provide controls how the Navigation button is displayed.
            setupActionBarWithNavController(navController, it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //handle on up button is pressed.
        return appBarConfiguration?.let {
            navController.navigateUp(it)
        }?:super.onSupportNavigateUp()
    }

    override fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean) {
        this.menuId = menuId
        this.onMenuItemClickListener = onMenuItemClickListener
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menuId == 0) {
            return super.onCreateOptionsMenu(menu)
        }

        menuInflater.inflate(menuId, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!::onMenuItemClickListener.isInitialized) {
            return super.onOptionsItemSelected(item)
        }

        //if menu id and nav's fragment id is same, then redirect
        if (navHostId != 0 && item.onNavDestinationSelected(navController)) {
            return true
        }

        if (onMenuItemClickListener(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setVariable(binding: DB) {
        binding.setVariable(BR.model, viewModel)
    }

    override fun navigate(id: Int) {
        navController.navigate(id)
    }

    override fun navigate(navDirections: NavDirections) {
        navController.navigate(navDirections)
    }

    fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle {
        return SavedStateViewModelFactory(
            app,
            savedStateRegistryOwner
        ).create(SavedStateViewModel::class.java)
            .savedStateHandle
    }

    inline fun <reified T : NavArgs> getNavArgs(): T = navArgs<T>().value


}