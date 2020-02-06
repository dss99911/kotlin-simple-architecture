package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.architecture.livedata.observeEvent
import kim.jeonghyeon.androidlibrary.extension.*

interface IBaseActivity : IBaseUi {
    /**
     * if you want to use nav controller, override this
     */
    val navHostId: Int
    val navController: NavController
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


    override val progressDialog by lazy { createProgressDialog() }

    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean


    override lateinit var binding: ViewDataBinding
    override val viewModels = mutableListOf<Pair<Int, Lazy<BaseViewModel>>>()
    /**
     * used for startActivityForResult
     */
    val rootViewModel: BaseViewModel
        get() = viewModels[0].second.value

    override var stateObserver: Observer<State> = resourceObserverCommon { }
        set(value) {
            val prev = field
            field = value

            viewModels.map { it.second.value }.forEach {
                it.state.removeObserver(prev)
                it.state.observe(field)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName} onCreate")

        setupView()
        setupActionbar()
        setupObserver()
        onViewModelSetup()
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
        log("${this::class.simpleName} onDestroy")
    }

    private fun setupView() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModels.forEach { (variableId, viewModel) ->
            binding.setVariable(variableId, viewModel.value)
        }
        binding.lifecycleOwner = this
    }

    private fun setupObserver() {
        //BaseActivity require at least one viewModel for startActivityForResult or permission.
        if (viewModels.isEmpty()) {
            viewModels.add(Pair(0, lazy { BaseViewModel() }))
        }

        viewModels.map { it.second.value }.forEach {
            it.state.observe(stateObserver)

            it.eventSnackbar.observeEvent {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            it.eventStartActivity.observeEvent {
                startActivity(it)
            }

            it.eventShowProgressBar.observeEvent {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }

            it.eventPerformWithActivity.observe { array ->
                array.forEach { event ->
                    if (!event.handled) {
                        event.handle()(this)
                    }
                }
            }

            it.eventNavDirectionId.observeEvent {
                navigate(it)
            }

            it.eventNav.observeEvent { action ->
                action(navController)
            }

            it.eventNavDirection.observeEvent {
                it.navigate()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        rootViewModel.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        rootViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
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