package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import kim.jeonghyeon.kotlinlibrary.type.WeakArrayList;

@SuppressWarnings({"WeakerAccess", "SameReturnValue", "unused"})
public class BaseRecyclerViewModel<VM> extends ViewModel implements BaseRecyclerViewAdapter.OnItemClickListener<VM> {

    private final ArrayList<VM> itemViewModels = new ArrayList<>();
    private final WeakArrayList<OnDataSetChangedListener> mOnDataSetChangedListeners = new WeakArrayList<>();

    public @NonNull ArrayList<VM> getItemViewModels() {
        return itemViewModels;
    }

    public void setItems(@NonNull ArrayList<VM> itemViewModels) {
        this.itemViewModels.clear();
        addItems(itemViewModels);
    }

    public void addItems(@NonNull ArrayList<VM> itemViewModels) {
        this.itemViewModels.addAll(itemViewModels);
        notifyOnDataSetChanged();
    }

    public void addItem(VM itemViewModel) {
        getItemViewModels().add(itemViewModel);
        notifyOnDataSetChanged();
    }

    public void addItem(int index, VM itemViewModel) {
        getItemViewModels().add(index, itemViewModel);
        notifyOnDataSetChanged();
    }

    public int removeItem(VM item) {
        int i = itemViewModels.indexOf(item);
        if (i == -1) {
            return i;
        }

        itemViewModels.remove(i);
        notifyOnDataSetChanged();

        return i;
    }

    @Override
    public void onItemClick(BaseRecyclerViewAdapter<VM> adapter, View view, int position) {

    }

    public void addOnDataSetChangedListener(@Nullable OnDataSetChangedListener listener) {
        if (listener == null) {
            return;
        }

        if (mOnDataSetChangedListeners.contains(listener)) {
            return;
        }

        mOnDataSetChangedListeners.addWeakReference(listener);
    }


    public void removeOnDataSetChangedListener(@Nullable OnDataSetChangedListener listener) {
        if (listener == null) {
            return;
        }
        mOnDataSetChangedListeners.removeWeakReference(listener);
    }

    public void notifyOnDataSetChanged() {
        mOnDataSetChangedListeners.forEachWeakReference(onDataSetChangedListener -> {
            onDataSetChangedListener.onDataSetChanged();
            return null;
        });
    }
}