package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import kim.jeonghyeon.androidlibrary.BR

class BasePagedListAdapter<VM : DiffComparable<VM>>(pagedList: PagedList<VM>, @LayoutRes val itemLayoutId: Int) :
    PagedListAdapter<VM, BaseRecyclerViewHolder<VM>>(object : DiffUtil.ItemCallback<VM>() {
        override fun areItemsTheSame(oldItem: VM, newItem: VM) = oldItem.areItemsTheSame(newItem)
        override fun areContentsTheSame(oldItem: VM, newItem: VM) = oldItem.areContentsTheSame(newItem)
    }) {

    var onItemClickListener: OnItemClickListener<VM>? = null

    init {
        submitList(pagedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<VM> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, itemLayoutId, parent, false)

        val simpleViewHolder = object : BaseRecyclerViewHolder<VM>(binding) {
            override fun getViewModelId(): Int {
                return this@BasePagedListAdapter.getViewModelId()
            }
        }

        simpleViewHolder.setOnClickListener {
            onItemClickListener?.onItemClick(
                this@BasePagedListAdapter,
                simpleViewHolder.itemView,
                simpleViewHolder.adapterPosition
            )
        }

        return simpleViewHolder
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder<VM>, position: Int) {
        holder.bind(getItem(position))
    }

    protected fun getViewModelId(): Int = BR.viewModel

    interface OnItemClickListener<VM : DiffComparable<VM>> {
        fun onItemClick(adapter: BasePagedListAdapter<VM>, view: View, position: Int)
    }
}
