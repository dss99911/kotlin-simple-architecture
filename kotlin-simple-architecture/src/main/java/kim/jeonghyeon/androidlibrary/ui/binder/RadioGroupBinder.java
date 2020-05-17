package kim.jeonghyeon.androidlibrary.ui.binder;

import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

/**
 * Created by hyun.kim on 26/10/17.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class RadioGroupBinder {
    @BindingAdapter("selectedResId")
    public static void selectRadio(RadioGroup view, int resId) {
        view.check(resId);
    }

    @InverseBindingAdapter(attribute = "selectedResId", event = "onCheckedChanged")
    public static int getSelectedRadioResId(RadioGroup view) {
        return view.getCheckedRadioButtonId();
    }


    @BindingAdapter("onCheckedChanged")
    public static void setOnCheckedChanged(RadioGroup view, @NonNull final InverseBindingListener onCheckedChanged) {
        view.setOnCheckedChangeListener((group, checkedId) -> onCheckedChanged.onChange());
    }
}
