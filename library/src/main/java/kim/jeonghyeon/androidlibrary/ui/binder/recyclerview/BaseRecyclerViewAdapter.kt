package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import kim.jeonghyeon.androidlibrary.BR

/**
 * Created by hyun.kim on 27/12/17.
 *
 *
 * This is for using RecyclerView without implementing RecyclerViewAdapter by developer.
 * refer to {[RecyclerViewBinder.bindRecyclerView]}
 *
 * limitation
 * - if item count is different with list size. just extending this class may not work properly. need to test
 */
abstract class BaseRecyclerViewAdapter<VM : DiffComparable<VM>> :
    ListAdapter<VM, BaseRecyclerViewHolder<VM>>(object : DiffUtil.ItemCallback<VM>() {
        override fun areItemsTheSame(oldItem: VM, newItem: VM): Boolean =
            oldItem.areItemsTheSame(newItem)

        override fun areContentsTheSame(oldItem: VM, newItem: VM): Boolean =
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

        return object : BaseRecyclerViewHolder<VM>(binding) {

            override fun getViewModelId(): Int {
                return this@BaseRecyclerViewAdapter.viewModelId
            }
        }
    }

    final override fun onBindViewHolder(holder: BaseRecyclerViewHolder<VM>, position: Int) {
        holder.bind(getItem(position))
    }

    final override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder<VM>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }
}