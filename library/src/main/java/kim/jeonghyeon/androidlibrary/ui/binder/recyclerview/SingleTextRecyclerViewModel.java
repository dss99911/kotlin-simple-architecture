package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

@SuppressWarnings({"unused"})
public class SingleTextRecyclerViewModel extends SelectableRecyclerViewModel<SingleTextRecyclerViewItemModel> {
    public SingleTextRecyclerViewModel(String... texts) {
        for (String text : texts) {
            SingleTextRecyclerViewItemModel itemViewModel = new SingleTextRecyclerViewItemModel();
            itemViewModel.text.setValue(text);
            addItem(itemViewModel);
        }
    }
}
