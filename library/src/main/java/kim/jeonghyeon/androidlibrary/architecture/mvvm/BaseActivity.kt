package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
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
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.extension.*

interface IBaseActivity {
    /**
     * viewModel name should be "model" for auto binding
     * if you'd like to change it, override setVariable
     */
    var binding: ViewDataBinding

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

    fun addFragment(container: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null)
}

abstract class BaseActivity : AppCompatActivity(), IBaseActivity {

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


    override lateinit var binding: ViewDataBinding
    val viewModels = mutableMapOf<Int, Lazy<BaseViewModel>>()
    /**
     * used for startActivityForResult
     */
    lateinit var rootViewModel: Lazy<BaseViewModel>

    override var stateObserver: Observer<ResourceState> = resourceObserverCommon {  }
        set(value) {
            val prev = field
            field = value

            viewModels.values.map { it.value }.forEach {
                it.state.removeObserver(prev)
                it.state.observe(this, field)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName} onCreate")

        setupView()
        setupActionbar()
        setupObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName} onDestroy")
    }

    private fun setupView() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModels.forEach { (variableId, viewModel) ->
            binding.setVariable(variableId, viewModel.value)
        }
        binding.lifecycleOwner = this
    }

    inline fun <reified V : BaseViewModel> addingViewModel(
        variableId: Int = BR.model,
        noinline viewModel: () -> V
    ): Lazy<V> {
        return viewModels<V> { InstanceViewModelFactory(viewModel) }.also {
            if (!::rootViewModel.isInitialized) {
                rootViewModel = it
            }
            viewModels[variableId] = it
        }
    }

    private fun setupObserver() {
        viewModels.values.map { it.value }.forEach {
            it.state.observe(this, stateObserver)

            it.eventToast.observeEvent(this) {
                toast(it)
            }

            it.eventSnackbar.observeEvent(this) {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            it.eventStartActivity.observeEvent(this) {
                startActivity(it)
            }

            it.eventShowProgressBar.observeEvent(this) {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }

            it.eventAddFragment.observeEvent(this) {
                addFragment(it.containerId, it.fragment, it.tag)
            }

            it.eventReplaceFragment.observeEvent(this) {
                replaceFragment(it.containerId, it.fragment, it.tag)
            }

            it.eventPerformWithActivity.observe(this) { array ->
                array.forEach { event ->
                    if (!event.hasBeenHandled) {
                        event.popContent()(this)
                    }
                }
            }

            it.eventNavDirectionId.observeEvent(this) {
                navigate(it)
            }

            it.eventNav.observeEvent(this) { action ->
                action(navController)
            }

            it.eventNavDirection.observeEvent(this) {
                navigate(it)
            }

            lifecycle.addObserver(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        rootViewModel.value.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        rootViewModel.value.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    /**
     * @param tag to find fragment by tag
     */
    override fun addFragment(container: Int, fragment: Fragment, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.add(container, fragment)
        } else {
            transaction.add(container, fragment, tag)
        }
        transaction.commitNow()
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun replaceFragment(container: Int, fragment: Fragment, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.replace(container, fragment)
        } else {
            transaction.replace(container, fragment, tag)
        }
        transaction.commitNow()
    }
}