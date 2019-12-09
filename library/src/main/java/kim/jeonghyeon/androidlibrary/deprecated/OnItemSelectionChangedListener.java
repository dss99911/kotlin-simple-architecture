package kim.jeonghyeon.androidlibrary.deprecated;

@SuppressWarnings({"EmptyMethod", "unused"})
public interface OnItemSelectionChangedListener<ITEM extends SelectableRecyclerViewItemViewModel> {
    void onItemSelectionChanged(SelectableRecyclerViewModel<ITEM> listViewModel, ITEM itemViewModel, int position);
}