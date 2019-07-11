package kim.jeonghyeon.androidlibrary.ui.binder;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kim.jeonghyeon.androidlibrary.R;
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BasePagedListAdapter;
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BaseRecyclerViewAdapter;
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BaseRecyclerViewModel;
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable;

/**
 * Created by hyun.kim on 27/12/17.
 * <p>
 * Databinding Binders for RecyclerView
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class RecyclerViewBinder {

    /**
     * set list of item viewModel and item layout, then no need to implement RecyclerViewAdapter.
     * on the layout, viewModel name should be 'viewModel'
     *
     * if you want other layout manager, use app:layoutManager="LinearLayoutManager" over than the attributes
     *
     *
     * limitation
     *  - no use DiffUtil
     *  - if list is totally changed, clear the previous list, and add items to previous list
     *
     * @param view       recyclerView
     * @param viewModel  recyclerviewModel
     * @param resourceId layout that refers data of viewmodel
     * @param <VM>       viewmodel class
     */
    @BindingAdapter({"viewModel", "itemResId"})
    public static <VM> void bindRecyclerView(RecyclerView view, BaseRecyclerViewModel<VM> viewModel, int resourceId) {
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (layoutManager == null) {
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        if (view.getAdapter() == null) {
            BaseRecyclerViewAdapter<VM> adapter = new BaseRecyclerViewAdapter<>(viewModel.getItemViewModels(), resourceId);
            view.setAdapter(adapter);
            viewModel.addOnDataSetChangedListener(adapter::notifyDataSetChanged);
            adapter.setOnClickListener(viewModel);
        }
    }

    @BindingAdapter({"viewModel"})
    public static <VM> void bindRecyclerView(@NonNull RecyclerView view, @NonNull BaseRecyclerViewModel<VM> viewModel) {
        bindRecyclerView(view, viewModel, R.layout.simple_list_item_1);
    }

    /**
     * consider itemResId is not changeable
     */
    @BindingAdapter({"pagedList", "itemResId"})
    public static <VM extends DiffComparable<VM>> void bindRecyclerView(@NonNull RecyclerView view, PagedList<VM> list, @LayoutRes int itemLayoutId) {
        bindRecyclerView(view,list,itemLayoutId, null);
    }

    /**
     * consider itemResId is not changeable
     */
    @BindingAdapter({"pagedList", "itemResId", "onItemClick"})
    public static <VM extends DiffComparable<VM>> void bindRecyclerView(@NonNull RecyclerView view, PagedList<VM> list, @LayoutRes int itemLayoutId, BasePagedListAdapter.OnItemClickListener<VM> listener) {
        if (list == null) {
            return;
        }
        if (view.getAdapter() == null) {
            BasePagedListAdapter<VM> adapter = new BasePagedListAdapter<>(list, itemLayoutId);
            view.setAdapter(adapter);
        }

        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter instanceof BasePagedListAdapter) {
            BasePagedListAdapter<VM> basePagedListAdapter = (BasePagedListAdapter<VM>) adapter;
            basePagedListAdapter.submitList(list);
            basePagedListAdapter.setOnItemClickListener(listener);
        }
    }


}
