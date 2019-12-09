package kim.jeonghyeon.androidlibrary.deprecated;

import androidx.lifecycle.MutableLiveData;

@SuppressWarnings("WeakerAccess")
public class SingleTextRecyclerViewItemModel extends SelectableRecyclerViewItemViewModel {
    public final MutableLiveData<String> text = new MutableLiveData<>();
}
