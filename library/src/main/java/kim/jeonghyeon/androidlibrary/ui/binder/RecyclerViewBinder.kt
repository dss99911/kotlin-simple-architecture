package kim.jeonghyeon.androidlibrary.ui.binder

import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BasePagedListAdapter
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BaseRecyclerViewAdapter
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

/**
 * set list of item viewModel and item layout, then no need to implement RecyclerViewAdapter.
 * on the layout, viewModel name should be 'viewModel'
 *
 * if you want other layout manager, use app:layoutManager="LinearLayoutManager" over than the attributes
 *
 * @param view       recyclerView
 * @param layoutId layout that refers data of viewmodel
 * @param <VM>       viewmodel class
</VM> */
@BindingAdapter("itemList", "itemLayoutId")
fun <VM : DiffComparable<VM>> bindRecyclerView(
    view: RecyclerView,
    itemList: List<VM>?,
    layoutId: Int
) {
    if (itemList == null) return

    val layoutManager = view.layoutManager
    if (layoutManager == null) {
        view.layoutManager = LinearLayoutManager(view.context)
    }

    @Suppress("UNCHECKED_CAST") val adapter =
        view.adapter as? BaseRecyclerViewAdapter<VM> ?: (object : BaseRecyclerViewAdapter<VM>() {
            override fun getItemLayoutId(position: Int) = layoutId
        }.also { view.adapter = it })

    adapter.submitList(itemList)
}

@BindingAdapter("itemList", "itemLayoutId")
fun <VM : DiffComparable<VM>> bindRecyclerView(
    view: RecyclerView,
    itemList: PagedList<VM>?,
    resourceId: Int
) {
    if (itemList == null) return

    val layoutManager = view.layoutManager
    if (layoutManager == null) {
        view.layoutManager = LinearLayoutManager(view.context)
    }

    @Suppress("UNCHECKED_CAST") val adapter =
        view.adapter as? BasePagedListAdapter<VM> ?: (object : BasePagedListAdapter<VM>() {
            override fun getLayoutId(viewType: Int): Int = resourceId
        }.also { view.adapter = it })

    adapter.submitList(itemList)
}