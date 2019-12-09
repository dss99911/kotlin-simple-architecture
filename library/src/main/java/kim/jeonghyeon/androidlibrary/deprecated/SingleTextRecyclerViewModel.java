package kim.jeonghyeon.androidlibrary.deprecated;

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
