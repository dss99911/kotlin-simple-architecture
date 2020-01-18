package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.app.AlertDialog
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.extension.dismissWithoutException
import kim.jeonghyeon.androidlibrary.extension.showSnackbar
import kim.jeonghyeon.androidlibrary.extension.showWithoutException

interface IBasePage : LifecycleOwner {
    /**
     * viewModel name should be "model" for auto binding
     * if you'd like to change it, override setVariable
     */
    var binding: ViewDataBinding
    val viewModels: MutableMap<Int, Lazy<BaseViewModel>>
    val layoutId: Int

    /**
     * toolbar id is "toolbar"
     * If you want to change, override this property
     */
    val toolbarId: Int
    val appBarConfiguration: AppBarConfiguration?

    fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean)

    fun navigate(@IdRes id: Int)
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
    var stateObserver: Observer<State>

    val progressDialog: AlertDialog
}

fun <T> IBasePage.resourceObserverCommon(onSuccess: (T) -> Unit): Observer<Resource<T>> =
    Observer {
        if (it.isLoading()) {
            progressDialog.showWithoutException()
        } else {
            progressDialog.dismissWithoutException()
        }

        it.onError {
            binding.root.showSnackbar("error occurred", Snackbar.LENGTH_SHORT)
        }

        it.onSuccess(onSuccess)
    }