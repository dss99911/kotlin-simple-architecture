package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import kim.jeonghyeon.androidlibrary.BR

abstract class BasePagedListAdapter<VM : DiffComparable<VM>>(val lifecycleOwner: LifecycleOwner? = null) :
    PagedListAdapter<VM, BaseRecyclerViewHolder<VM>>(object : DiffUtil.ItemCallback<VM>() {
        override fun areItemsTheSame(oldItem: VM, newItem: VM) = oldItem.areItemsTheSame(newItem)
        override fun areContentsTheSame(oldItem: VM, newItem: VM) =
            oldItem.areContentsTheSame(newItem)
    }) {

    @LayoutRes
    abstract fun getItemLayoutId(position: Int): Int

    final override fun getItemViewType(position: Int): Int = getItemLayoutId(position)

    /**
     * if viewModel Id is different. override this
     */
    protected val viewModelId: Int
        get() = BR.model


    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder<VM> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            viewType,
            parent,
            false
        )

        return object : BaseRecyclerViewHolder<VM>(binding, lifecycleOwner) {
            override val viewModelId: Int = this@BasePagedListAdapter.viewModelId
        }
    }

    final override fun onBindViewHolder(holder: BaseRecyclerViewHolder<VM>, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    final override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder<VM>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }
}
