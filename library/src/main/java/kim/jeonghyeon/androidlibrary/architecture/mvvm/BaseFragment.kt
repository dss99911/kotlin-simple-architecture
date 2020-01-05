package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
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
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.extension.*
import org.jetbrains.anko.support.v4.toast

/**
 * Methods
 * - setMenu()
 */

interface IBaseFragment {
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

    fun navigate(@IdRes id : Int)
    fun navigate(navDirections: NavDirections)

    /**
     * when observe LiveData, override this
     */
    fun onViewModelSetup()

//  fun getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle
//fun <reified T : NavArgs> getNavArgs(): T

    /**
     * set state observer to change loading and error on state liveData
     */
    var stateObserver: Observer<ResourceState>

    /**
     * used on pager. if not used always true.
     */
    var selected: Boolean
    /**
     * whether fragment is visible.
     * considering fragment lifecycle and pager
     */
    var visible: Boolean

    fun addFragment(container: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null)
}

abstract class BaseFragment : Fragment(),
    IBaseFragment {
    override lateinit var binding: ViewDataBinding
    val viewModels = mutableMapOf<Int, Lazy<BaseViewModel>>()

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
            val prev = field
            field = value

            viewModels.values.map { it.value }.forEach {
                it.state.removeObserver(prev)
                it.state.observe(this, field)
            }
        }

    /**
     * used on pager. if not used always true.
     */
    override var selected = true
        set(value) {
            field = value
            visible = isVisible(value, lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        }

    override var visible = false
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
    }

    override fun onPause() {
        super.onPause()
        log("${this::class.simpleName}")
    }

    override fun onStop() {
        super.onStop()

        visible = isVisible(selected, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName}")
    }

    inline fun <reified V : BaseViewModel> addingViewModel(
        ownerProducer: ViewModelStoreOwner = this,
        variableId: Int = BR.model,
        noinline viewModel: () -> V
    ): Lazy<V> {
        return viewModels<V>({ ownerProducer }, { InstanceViewModelFactory(viewModel) }).also {
            viewModels[variableId] = it
        }
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
                    progressDialog?.showWithoutException()
                } else {
                    progressDialog?.dismissWithoutException()
                }
            }

            it.eventAddFragment.observeEvent(this) {
                addFragment(it.containerId, it.fragment, it.tag)
            }

            it.eventNavDirectionId.observeEvent(this) {
                navigate(it)
            }

            it.eventNav.observeEvent(this) { action ->
                action(findNavController())
            }

            it.eventNavDirection.observeEvent(this) {
                navigate(it)
            }

            it.eventReplaceFragment.observeEvent(this) {
                replaceFragment(it.containerId, it.fragment, it.tag)
            }

            it.eventPerformWithActivity.observe(this) { array ->
                array.forEach { event ->
                    if (!event.hasBeenHandled) {
                        event.popContent()(requireActivity() as BaseActivity)
                    }
                }

            }

            lifecycle.addObserver(it)

            onViewModelSetup()
        }
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

    override fun onViewModelSetup() {

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
    fun <A : BaseViewModel> getActivityViewModel(variableId: Int = BR.model): A {
        return (requireActivity() as BaseActivity).viewModels[variableId]!!.value as A
    }

    inline fun <reified T : NavArgs> getNavArgs(): T = navArgs<T>().value


    override fun onStart() {
        super.onStart()

        visible = isVisible(selected, true)
    }

    private fun isVisible(selected: Boolean, isStarted: Boolean): Boolean = selected && isStarted

    open fun onVisibilityChanged(visible: Boolean) {
        log("${this::class.simpleName} : $visible")
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun addFragment(container: Int, fragment: Fragment, tag: String?) {
        (activity as? BaseActivity)?.addFragment(container, fragment, tag)
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun replaceFragment(container: Int, fragment: Fragment, tag: String?) {
        (activity as? BaseActivity)?.replaceFragment(container, fragment, tag)
    }
}