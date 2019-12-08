package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import kim.jeonghyeon.androidlibrary.BR
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.ui.binder.RecyclerViewBinder

/**
 * Created by hyun.kim on 27/12/17.
 *
 *
 * This is for using RecyclerView without implementing RecyclerViewAdapter by developer.
 * refer to {[RecyclerViewBinder.bindRecyclerView]}
 */
abstract class BaseRecyclerViewAdapter2<VM : BaseViewModel>(
    diffCallback: DiffUtil.ItemCallback<VM>
) : ListAdapter<VM, BaseRecyclerViewHolder<VM>>(diffCallback) {


    @LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    private var onItemClickListener: OnItemClickListener<VM>? = null

    /**
     * if viewModel Id is different. override this
     */
    protected val viewModelId: Int
        get() = BR.model

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<VM> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, getLayoutId(viewType), parent, false)

        val viewHolder = object : BaseRecyclerViewHolder<VM>(binding) {

            override fun getViewModelId(): Int {
                return this@BaseRecyclerViewAdapter2.viewModelId
            }
        }

        viewHolder.setOnClickListener { v ->
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(
                    this@BaseRecyclerViewAdapter2,
                    viewHolder.itemView,
                    viewHolder.adapterPosition
                )
            }
        }

        return viewHolder
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

    fun setOnClickListener(onItemClickListener: OnItemClickListener<VM>) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener<VM : BaseViewModel> {
        fun onItemClick(adapter: BaseRecyclerViewAdapter2<VM>, view: View, position: Int)
    }

}
