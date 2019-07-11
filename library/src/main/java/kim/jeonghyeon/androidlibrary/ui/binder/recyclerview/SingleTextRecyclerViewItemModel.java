package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import androidx.lifecycle.MutableLiveData;

@SuppressWarnings("WeakerAccess")
public class SingleTextRecyclerViewItemModel extends SelectableRecyclerViewItemViewModel {
    public final MutableLiveData<String> text = new MutableLiveData<>();
}
