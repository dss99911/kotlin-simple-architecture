package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.annotation.MenuRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.architecture.livedata.observeEvent
import kim.jeonghyeon.androidlibrary.extension.createProgressDialog
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showWithoutException
import kim.jeonghyeon.androidlibrary.util.log
import kim.jeonghyeon.common.extension.letIf

/**
 *
 * [layoutId] : override and input activity layout. if you don't want to show layout. set as 0
 *
 * [binding] : viewModel name should be "model" for auto binding, if you'd like to change it, override setVariable.
 *
 * [bindingViewModel] : bind view and viewModel
 *                      val viewModel by bindingViewModel<>()
 *
 * [toolbarId] : if you use toolbar, define toolbar's id as 'toolbar' or override this property
 *
 * [appBarConfiguration] : for controlling top page(showing up button or drawer button)
 *
 * [setMenu] : set menu
 *
 * [showSnackbar] : show snackbar
 *
 * [navigate] : navigate
 *              fun navigate(@IdRes id: Int) : with action Id.
 *              fun NavDirections.navigate() : with NavDirections
 * [navController] : use this if [navigate] is not enough
 *
 * [navHostId] : if you want to use nav controller, override this
 *
 * [onViewModelSetup] : override this when observe viewModel's liveData
 *
 * [observe] : observe livedata
 *            fun <T> AliveData<T>.observe(onChanged: (T) -> Unit)
 *            fun <T> AliveData<T>.observe(observer: Observer<in T>)
 *
 * [observeEvent] : observe one time event like redirect to other page or show popup,
 *            fun <T> AliveData<T>.observeEvent(onChanged: (T) -> Unit)
 *            fun <T> AliveData<T>.observeEvent(observer: Observer<in T>)
 *
 * [stateObserver] : set state observer to change loading and error on state liveData
 *
 * [initStateObserver] : set state observer to change loading and error on initState liveData
 *
 * [progressDialog] : progress dialog. override this if need to change loading ui
 *
 */
abstract class BaseActivity : AppCompatActivity(), IBaseUi {

    override val viewContext: Context? get() = this
    open val navHostId: Int = 0
    override val navController: NavController
        get() {
            require(navHostId != 0) { "navHostId is not set" }
            return findNavController(navHostId)
        }

    override val toolbarId: Int
        get() = R.id.toolbar

    override val appBarConfiguration: AppBarConfiguration?
        get() = if (navHostId != 0) AppBarConfiguration(navController.graph) else null


    private val progressDialogLazy = lazy { createProgressDialog() }
    override val progressDialog by progressDialogLazy

    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean


    override lateinit var binding: ViewDataBinding
    override val viewModels = mutableListOf<Pair<Int, Lazy<BaseViewModel>>>()

    val permissionStartActivityViewModel by viewModels<PermissionAndStartActivityViewModel>()

    override val stateObserver: Observer<State> by lazy { resourceObserverCommon() }
    override val initStateObserver: Observer<State> by lazy { resourceObserverInit() }

    override val savedState by savedState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")

        setupView()
        setupActionbar()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupObserver()
    }

    override fun onViewModelSetup() {
    }

    override fun onStart() {
        super.onStart()
        viewModels.map { it.second.value }.forEach {
            it.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModels.map { it.second.value }.forEach {
            it.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModels.map { it.second.value }.forEach {
            it.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModels.map { it.second.value }.forEach {
            it.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        if (progressDialogLazy.isInitialized()) {
            progressDialog.dismissWithoutException()
        }

        dismissErrorSnackbar()
    }

    private fun setupView() {
        val layoutId = layoutId.letIf(layoutId == 0) { R.layout.empty_layout }

        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModels.forEach { (variableId, viewModel) ->
            binding.setVariable(variableId, viewModel.value)
        }
        binding.lifecycleOwner = this
    }

    private fun setupObserver() {
        permissionStartActivityViewModel.eventPerformWithActivity { array ->
            array.forEach { event ->
                if (!event.handled) {
                    event.handle()(this)
                }
            }
        }

        viewModels.map { it.second.value }.forEach {
            it.state(stateObserver)
            it.initState(initStateObserver)

            it.eventSnackbarByString(true) {
                showSnackbar(it)
            }

            it.eventSnackbarById(true) {
                showSnackbar(getString(it))
            }

            it.eventStartActivity(true) {
                startActivity(it)
            }

            it.eventShowProgressBar(true) {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }
            it.eventShowOkDialog(true) {
                showOkDialog(it.message, it.onClick)
            }

            it.eventNav(true) { action ->
                action(navController)
            }
            it.eventStartActivityForResult(true) {
                permissionStartActivityViewModel.startActivityForResult(it.intent, it.onResult)
            }
            it.eventRequestPermission(true) {
                permissionStartActivityViewModel.requestPermissions(it.permissions, it.listener)
            }
            it.eventPermissionSettingPage(true) {
                permissionStartActivityViewModel.startPermissionSettingsPage(it)
            }
            it.eventFinish(true) {
                finish()
            }
            it.eventFinishWithResult(true) {
                if (it.data == null) {
                    setResult(it.resultCode)
                } else {
                    setResult(it.resultCode, it.data)
                }
                finish()
            }
        }

        onViewModelSetup()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionStartActivityViewModel.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionStartActivityViewModel.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
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

    override fun NavDirections.navigate() {
        navController.navigate(this)
    }

    override fun <T> LiveObject<T>.observe(onChanged: (T) -> Unit) {
        observe(this@BaseActivity, onChanged)
    }

    override fun <T> LiveObject<T>.observeEvent(onChanged: (T) -> Unit) {
        observeEvent(this@BaseActivity, onChanged)
    }

    override fun <T> LiveObject<T>.observeEvent(observer: Observer<in T>) {
        observeEvent(this@BaseActivity, observer)
    }

    override fun <T> LiveObject<T>.observe(observer: Observer<in T>) {
        observe(this@BaseActivity, observer)
    }
}