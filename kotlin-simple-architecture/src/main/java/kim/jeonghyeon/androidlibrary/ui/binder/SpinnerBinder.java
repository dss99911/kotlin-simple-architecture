package kim.jeonghyeon.androidlibrary.ui.binder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import java.util.ArrayList;

/**
 * Created by hyun.kim on 26/10/17.
 * <p>
 * Databinding Binders for Spinner
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class SpinnerBinder {
    /**
     * set List on layout.
     *
     * @param view               Spinner
     * @param list               list of items which will be converted to String
     * @param resourceId         view resource id
     * @param dropdownResourceId dropdown resource id.
     * @param <T>                object type to be converted to string.
     */
    @BindingAdapter({"android:entries", "itemLayoutId", "dropdownResId"})
    public static <T> void bindSpinner(Spinner view, @NonNull ArrayList<T> list, int resourceId, int dropdownResourceId) {
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<>(view.getContext(), resourceId, list);
        arrayAdapter.setDropDownViewResource(dropdownResourceId);
        view.setAdapter(arrayAdapter);
    }

    @BindingAdapter({"android:entries", "itemLayoutId", "dropdownResId"})
    public static <T> void bindSpinnerArray(Spinner view, @NonNull T[] list, int resourceId, int dropdownResourceId) {
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<>(view.getContext(), resourceId, list);
        arrayAdapter.setDropDownViewResource(dropdownResourceId);
        view.setAdapter(arrayAdapter);
    }

    /**
     * set selection of Spinner
     *
     * @param view     Spinner
     * @param position the index of selected item of spinner
     */
    @BindingAdapter("android:selection")
    public static void setSelection(final Spinner view, final int position) {
        //after layout completed, setSelection should be called. that's why post() is used.
        view.post(() -> view.setSelection(position));

    }

    /**
     * user action will be reflected to viewModel.
     *
     * @param view spinner
     * @return selection value
     */
    @InverseBindingAdapter(attribute = "android:selection", event = "onItemSelected")
    public static int getSelection(Spinner view) {
        return view.getSelectedItemPosition();
    }

    /**
     * when this event happen, inverseBinding will work.
     *
     * @param view           Spinner
     * @param onItemSelected on user action.
     */
    @BindingAdapter("onItemSelected")
    public static void setOnCheckedChanged(Spinner view, @NonNull final InverseBindingListener onItemSelected) {
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected.onChange();
            }

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
