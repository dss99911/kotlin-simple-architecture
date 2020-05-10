package kim.jeonghyeon.androidlibrary.ui.binder

import android.graphics.drawable.Drawable
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kim.jeonghyeon.androidlibrary.extension.bindData
import kim.jeonghyeon.androidlibrary.extension.bindDiffComparable
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DividerItemDecoration

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
fun <VM : Any> RecyclerView.bindData(
    itemList: List<VM>?,
    layoutId: Int
) {
    bindData(itemList, layoutId, null)
}

/**
 * use DiffComparable<VM> if invalidate item ui only when it is changed.
 */
@BindingAdapter("itemListComparable", "itemLayoutId")
fun <VM : DiffComparable<VM>> RecyclerView.bindDiffComparable(
    itemList: List<VM>?,
    layoutId: Int
) {
    bindDiffComparable(itemList, layoutId, null)
}


/**
 * for PagedList
 */
@BindingAdapter("itemList", "itemLayoutId")
fun <VM : Any> RecyclerView.bindData(
    itemList: PagedList<VM>?,
    layoutId: Int
) {
    bindData(itemList, layoutId, null)
}

/**
 * for PagedList, DiffComparable
 */
@BindingAdapter("itemListComparable", "itemLayoutId")
fun <VM : DiffComparable<VM>> RecyclerView.bindDiffComparable(
    itemList: PagedList<VM>?,
    layoutId: Int
) {
    bindDiffComparable(itemList, layoutId, null)
}

@BindingAdapter("itemDividerDrawable")
fun RecyclerView.setItemDevider(drawable: Drawable) {
    val layoutManager = layoutManager
    if (layoutManager !is LinearLayoutManager) {
        error("doesn't support $layoutManager")
    }

    DividerItemDecoration(context, layoutManager.orientation, false).apply {
        setDrawable(drawable)
    }.let { addItemDecoration(it) }
}