package kim.jeonghyeon.androidlibrary.ui.binder

import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import kim.jeonghyeon.androidlibrary.extension.bindData
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

/**
 * set list of item viewModel and item layout, then no need to implement RecyclerViewAdapter.
 * on the layout, viewModel name should be 'model'
 *
 * - if you want other layout manager, use app:layoutManager="LinearLayoutManager" over than the attributes
 *
 * - this doesn't use lifecycle. so, don't use LiveData, just use variable.
 *   if you have to use LiveData, use bindData(itemList, layoutId, lifecycleOwner) on Fragment or Activity side
 *
 * @param itemList  Item Model
 * @param layoutId  Item's layout id
 * */
@BindingAdapter("itemList", "itemLayoutId")
fun <VM : DiffComparable<VM>> RecyclerView.bindData(
    itemList: List<VM>?,
    layoutId: Int
) {
    bindData(itemList, layoutId, null)
}

@BindingAdapter("itemList", "itemLayoutId")
fun <VM : DiffComparable<VM>> RecyclerView.bindData(
    itemList: PagedList<VM>?,
    layoutId: Int
) {
    bindData(itemList, layoutId, null)
}