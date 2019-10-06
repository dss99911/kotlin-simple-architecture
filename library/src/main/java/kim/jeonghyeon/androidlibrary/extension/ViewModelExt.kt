package kim.jeonghyeon.androidlibrary.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.savedstate.SavedStateRegistryOwner
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmActivity
import kim.jeonghyeon.androidlibrary.architecture.mvvm.SimpleViewModel

/**
 * bind layoutId, viewModel
 */
inline fun <reified T : ViewDataBinding> FragmentActivity.bind(layoutId: Int, bind: (T) -> Unit): T =
        DataBindingUtil.setContentView<T>(this, layoutId)
                .also(bind)
                .also { it.lifecycleOwner = this }


/**
 * bind layoutId, viewModel
 */
inline fun <reified T : ViewDataBinding> Fragment.bind(inflater: LayoutInflater, parent: ViewGroup?, layoutId: Int, bind: (T) -> Unit): T =
        DataBindingUtil.inflate<T>(inflater, layoutId, parent, false)
                .also(bind)
                .also { it.lifecycleOwner = this }


/**
 * todo there is limitation, don't access parent and navArgs on init{ }.
 */
class SimpleViewModelFactory<P : ViewModel, A: NavArgs>(val parent: P? = null, val navArgs: A? = null, private val savedStateRegistryOwner: SavedStateRegistryOwner) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val newInstance = SavedStateViewModelFactory(app, savedStateRegistryOwner).create(modelClass) as SimpleViewModel<P, A>
        if (parent != null) {
            newInstance.parent = parent
        }

        if (navArgs != null) {
            newInstance.args = navArgs
        }

        return newInstance as T
    }
}

@MainThread
inline fun <reified A: NavArgs, reified P : ViewModel, reified V : SimpleViewModel<P, A>> Fragment.simpleViewModels(
    ownerProducer: ViewModelStoreOwner = this,
    savedStateRegistryOwner: SavedStateRegistryOwner = this
): Lazy<V> = viewModels({ ownerProducer }) {
    val activity = requireActivity()
    val parent = if (activity is MvvmActivity<*,*> && P::class != ViewModel::class) {
        activity.viewModel
    } else null

    val args = if (A::class == NavArgs::class) {
        null
    } else {
        navArgs<A>().value
    }

    SimpleViewModelFactory(parent, args, savedStateRegistryOwner)
}

@MainThread
inline fun <reified A: NavArgs, reified P : ViewModel, reified V : SimpleViewModel<P, A>> AppCompatActivity.simpleViewModels(): Lazy<V> =
    viewModels {
        val args = if (A::class == NavArgs::class) {
            null
        } else {
            navArgs<A>().value
        }

        SimpleViewModelFactory(null, args, this)
    }