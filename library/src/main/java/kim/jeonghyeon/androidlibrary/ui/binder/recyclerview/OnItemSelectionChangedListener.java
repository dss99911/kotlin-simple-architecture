package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

@SuppressWarnings({"EmptyMethod", "unused"})
public interface OnItemSelectionChangedListener<ITEM extends SelectableRecyclerViewItemViewModel> {
    void onItemSelectionChanged(SelectableRecyclerViewModel<ITEM> listViewModel, ITEM itemViewModel, int position);
}