package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.architecture.livedata.observeEvent
import kim.jeonghyeon.androidlibrary.extension.createProgressDialog
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.log
import kim.jeonghyeon.androidlibrary.extension.showWithoutException
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.Qualifier


/**
 *
 * [layoutId] : override and input fragment layout. if you don't want to show layout. set as 0
 *
 * [binding] : viewModel name should be "model" for auto binding, if you'd like to change it, override setVariable
 *
 * [bindingViewModel] : bind view and viewModel
 *                      val viewModel by bindingViewModel<>()
 *
 * [bindingActivityViewModel] : bind activity's view model
 *                      val viewModel by bindingActivityViewModel<>()
 *
 * [getActivityViewModel] : get activity's view model
 *                          val viewModel by bindingViewModel<> {
 *                              parameterOf(getActivityViewModel())
 *                          }
 * [getNavGraphViewModel] : get other fragment's view model
 *                          val viewModel by bindingViewModel<> {
 *                              parameterOf(getNavGraphViewModel<SomethingFragment>(R.id.fragment_something))
 *                          }
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
 * [stateObserver] : set state observer to change loading and error on state liveData. you can override this for changing error ui.
 *
 * [initStateObserver] : set state observer to change loading and error on initState liveData. you can override this for changing error ui.
 *
 * [progressDialog] : progress dialog. override this if need to change loading ui
 *
 * [selected] : used on pager. if not used always true.
 *
 * [visible] : whether fragment is visible. considering fragment lifecycle and pager
 */
abstract class BaseFragment : Fragment(),
    IBaseUi {
    override lateinit var binding: ViewDataBinding
    override val viewModels = mutableListOf<Pair<Int, Lazy<BaseViewModel>>>()
    override val viewContext: Context? get() = context
    @MenuRes
    private var menuId: Int = 0
    private lateinit var onMenuItemClickListener: (MenuItem) -> Boolean
    private val progressDialogLazy = lazy { createProgressDialog() }
    override val progressDialog by progressDialogLazy
    override val toolbarId: Int
        get() = R.id.toolbar
    override val navController: NavController by lazy { findNavController() }

    override val appBarConfiguration: AppBarConfiguration?
        get() = AppBarConfiguration(navController.graph)

    override val stateObserver: Observer<State> by lazy { resourceObserverCommon() }
    override val initStateObserver: Observer<State> by lazy { resourceObserverInit() }

    override val savedState by savedState()

    val permissionStartActivityViewModel by activityViewModels<PermissionAndStartActivityViewModel>()

    /**
     * used on pager. if not used always true.
     */
    var selected = true
        set(value) {
            field = value
            visible = isVisible(value, lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        }

    var visible = false
        set(value) {
            if (value != field) {
                onVisibilityChanged(value)
            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName}")
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (layoutId == 0) return null

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewModels.forEach { (variableId, viewModel) ->
            binding.setVariable(variableId, viewModel.value)
        }
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        log("${this::class.simpleName}")
        setupActionbar()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        log("${this::class.simpleName}")

        viewModels.map { it.second.value }.forEach {
            it.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        log("${this::class.simpleName}")

        viewModels.map { it.second.value }.forEach {
            it.onPause()
        }
    }

    override fun onStop() {
        super.onStop()

        visible = isVisible(selected, false)

        viewModels.map { it.second.value }.forEach {
            it.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName}")
        if (progressDialogLazy.isInitialized()) {
            progressDialog.dismissWithoutException()
        }

        dismissErrorSnackbar()
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
        if (item.onNavDestinationSelected(navController)) {
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

    private fun setupObserver() {
        viewModels.map { it.second.value }.forEach {
            it.state.observe(stateObserver)
            it.initState.observe(initStateObserver)

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
                activity?.finish()
            }
            it.eventFinishWithResult(true) {
                val activity = activity ?: return@eventFinishWithResult
                if (it.data == null) {
                    activity.setResult(it.resultCode)
                } else {
                    activity.setResult(it.resultCode, it.data)
                }
                activity.finish()
            }
        }

        onViewModelSetup()
    }

    private fun setupActionbar() {
        if (!::binding.isInitialized) {
            return
        }

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
            setupActionBarWithNavController(activity, navController, it)
        }
    }

    override fun onViewModelSetup() {

    }

    override fun navigate(id: Int) {
        navController.navigate(id)
    }

    override fun NavDirections.navigate() {
        navController.navigate(this)
    }

    override fun onStart() {
        super.onStart()

        visible = isVisible(selected, true)
        viewModels.map { it.second.value }.forEach {
            it.onStart()
        }
    }

    private fun isVisible(selected: Boolean, isStarted: Boolean): Boolean = selected && isStarted

    open fun onVisibilityChanged(visible: Boolean) {
        log("${this::class.simpleName} : $visible")
    }

    override fun <T> LiveObject<T>.observe(onChanged: (T) -> Unit) {
        observe(viewLifecycleOwner, onChanged)
    }

    override fun <T> LiveObject<T>.observeEvent(onChanged: (T) -> Unit) {
        observeEvent(viewLifecycleOwner, onChanged)
    }

    override fun <T> LiveObject<T>.observeEvent(observer: Observer<in T>) {
        observeEvent(viewLifecycleOwner, observer)
    }

    override fun <T> LiveObject<T>.observe(observer: Observer<in T>) {
        observe(viewLifecycleOwner, observer)
    }

    inline fun <reified V : BaseViewModel> bindingActivityViewModel(
        variableId: Int = BR.model,
        qualifier: Qualifier? = null
    ): Lazy<V> {
        return sharedViewModel<V>(qualifier).also {
            viewModels.add(Pair(variableId, it))
        }
    }

    inline fun <reified T : ViewModel> getActivityViewModel(
        qualifier: Qualifier? = null
    ): T = getSharedViewModel(qualifier)

    /**
     *    you can call different fragment's viewModel if it's in back stack
     */
    inline fun <reified T : ViewModel> getNavGraphViewModel(
        @IdRes navGraphId: Int
    ): T = navGraphViewModels<T>(navGraphId).value
}