package kim.jeonghyeon.androidlibrary.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T =
        ViewModelProviders.of(this).get(T::class.java)

/**
 * bind layoutId, viewModel
 */
inline fun <reified T : ViewDataBinding> FragmentActivity.bind(layoutId: Int, bind: (T) -> Unit): T =
        DataBindingUtil.setContentView<T>(this, layoutId)
                .also(bind)
                .also { it.setLifecycleOwner(this) }


/**
 * bind layoutId, viewModel
 */
inline fun <reified T : ViewDataBinding> Fragment.bind(inflater: LayoutInflater, parent: ViewGroup?, layoutId: Int, bind: (T) -> Unit): T =
        DataBindingUtil.inflate<T>(inflater, layoutId, parent, false)
                .also(bind)
                .also { it.setLifecycleOwner(this) }


