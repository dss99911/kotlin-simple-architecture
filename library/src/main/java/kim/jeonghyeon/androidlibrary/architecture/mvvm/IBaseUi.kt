package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.app.AlertDialog
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.ui.AppBarConfiguration
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.material.snackbar.Snackbar
import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.State
import kim.jeonghyeon.androidlibrary.extension.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

interface IBaseUi : SavedStateRegistryOwner {
    /**
     * viewModel name should be "model" for auto binding
     * if you'd like to change it, override setVariable
     */
    var binding: ViewDataBinding
    val viewModels: MutableList<Pair<Int, Lazy<BaseViewModel>>>
    val layoutId: Int

    /**
     * toolbar id is "toolbar"
     * If you want to change, override this property
     */
    val toolbarId: Int
    val appBarConfiguration: AppBarConfiguration?

    fun setMenu(@MenuRes menuId: Int, onMenuItemClickListener: (MenuItem) -> Boolean)

    fun navigate(@IdRes id: Int)
    fun NavDirections.navigate()

    fun showSnackbar(text: String) {
        binding.root.showSnackbar(text, Snackbar.LENGTH_SHORT)
    }

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

    fun <T> LiveObject<T>.observe(onChanged: (T) -> Unit)
    fun <T> LiveObject<T>.observeEvent(onChanged: (T) -> Unit)
    fun <T> LiveObject<T>.observeEvent(observer: Observer<in T>)
    fun <T> LiveObject<T>.observe(observer: Observer<in T>)
}

fun <T> IBaseUi.resourceObserverCommon(onSuccess: (T) -> Unit): Observer<Resource<T>> =
    Observer {
        if (it.isLoading()) {
            progressDialog.showWithoutException()
        } else {
            progressDialog.dismissWithoutException()
        }

        it.onError {
            showSnackbar(ctx.getString(R.string.error_occurred))
        }

        it.onSuccess(onSuccess)
    }

fun IBaseUi.getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle {
    return SavedStateViewModelFactory(
        app,
        savedStateRegistryOwner
    ).create(SavedStateViewModel::class.java)
        .savedStateHandle
}

internal class SavedStateViewModel(val savedStateHandle: SavedStateHandle) : ViewModel()

inline fun <reified V : BaseViewModel> IBaseUi.bindingViewModel(
    variableId: Int = BR.model,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<V> {
    return viewModel<V>(qualifier, parameters).also {
        viewModels.add(Pair(variableId, it))
    }
}