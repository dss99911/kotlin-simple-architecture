package kim.jeonghyeon.androidlibrary.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.navArgs
import kim.jeonghyeon.androidlibrary.architecture.mvvm.ArgumentViewModel

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


class ArgumentViewModelFactory<A : NavArgs, V : ArgumentViewModel<A>>(navArgs: NavArgsLazy<A>) :
        ViewModelProvider.Factory {
        val args: A by navArgs
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val newInstance = modelClass.newInstance() as V
                newInstance.args = args
                return newInstance as T
        }
}

@MainThread
inline fun <reified A : NavArgs, reified VM : ArgumentViewModel<A>> Fragment.argumentViewModels(
        ownerProducer: ViewModelStoreOwner = this
) = viewModels<VM>({ ownerProducer }) { ArgumentViewModelFactory<A, VM>(navArgs()) }