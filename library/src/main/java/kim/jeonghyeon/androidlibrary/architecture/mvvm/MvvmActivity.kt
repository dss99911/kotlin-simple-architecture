package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.BaseActivity
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

}

abstract class MvvmActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseActivity(), IMvvmActivity<VM, DB> {

    override val navHostId: Int = 0
    override val navController: NavController
        get() = findNavController(navHostId)
    override val appBarConfiguration: AppBarConfiguration?
        get() = if (navHostId != 0) AppBarConfiguration(navController.graph) else null


    private val progressDialog by lazy { createProgressDialog() }

    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean

    override val toolbarId: Int
        get() = R.id.toolbar


    override lateinit var binding: DB

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
            toast.observeEvent(this@MvvmActivity) {
                toast(it)
            }

            snackbar.observeEvent(this@MvvmActivity) {
                binding.root.showSnackbar(it, Snackbar.LENGTH_SHORT)
            }

            startActivity.observeEvent(this@MvvmActivity) {
                startActivity(it)
            }

            startActivityForResult.observeEvent(this@MvvmActivity) { (intent, requestCode) ->
                try {
                    startActivityForResult(intent, requestCode)
                } catch (e: IllegalStateException) {
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.toast_no_activity)
                }
            }

            showProgressBar.observeEvent(this@MvvmActivity) {
                if (it) {
                    progressDialog.showWithoutException()
                } else {
                    progressDialog.dismissWithoutException()
                }
            }

            addFragment.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.addFragment(it.containerId, it.fragment, it.tag)
            }

            replaceFragment.observeEvent(this@MvvmActivity) {
                this@MvvmActivity.replaceFragment(it.containerId, it.fragment, it.tag)
            }

            performWithActivity.observeEvent(this@MvvmActivity) {
                it(this@MvvmActivity)
            }

            navDirectionId.observeEvent(this@MvvmActivity) {
                navigate(it)
            }

            navDirection.observeEvent(this@MvvmActivity) {
                navigate(it)
            }

            onCreate()
        }
    }

    private fun setupActionbar() {
        val toolbar = findViewById<View>(R.id.toolbar)
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
        if (navHostId != 0 && item.onNavDestinationSelected(findNavController(navHostId))) {
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

    override fun navigate(id: Int) {
        require(navHostId != 0) { "navHostId is not set" }

        findNavController(navHostId).navigate(id)
    }

    override fun navigate(navDirections: NavDirections) {
        require(navHostId != 0) { "navHostId is not set" }

        findNavController(navHostId).navigate(navDirections)
    }
}