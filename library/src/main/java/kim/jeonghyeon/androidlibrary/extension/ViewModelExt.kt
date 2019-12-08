package kim.jeonghyeon.androidlibrary.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

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


class InstanceViewModelFactory<V : ViewModel> (val viewModel: () -> V) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel() as T
    }
}

inline fun <reified V : ViewModel> Fragment.simpleViewModels(ownerProducer: ViewModelStoreOwner = this, noinline viewModel: () -> V): Lazy<V> {
    return viewModels({ownerProducer}, {InstanceViewModelFactory(viewModel)})
}


inline fun <reified V : ViewModel> AppCompatActivity.simpleViewModels(noinline viewModel: () -> V): Lazy<V> {
    return viewModels {InstanceViewModelFactory(viewModel)}
}
