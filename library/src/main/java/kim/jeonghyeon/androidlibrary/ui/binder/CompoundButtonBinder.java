package kim.jeonghyeon.androidlibrary.ui.binder;


/**
 * Created by hyun.kim on 28/02/18.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class CompoundButtonBinder {
//    /**
//     * set checked of CompoundButton
//     */
//    @BindingAdapter("android:checked")
//    public static void setChecked(final CompoundButton view, final boolean checked) {
//        //after layout completed, setSelection should be called. that's why post() is used.
//        view.post(() -> view.setChecked(checked));
//    }
//
//    /**
//     * user action will be reflected to viewModel.
//     *
//     * @param view spinner
//     * @return selection value
//     */
//    @InverseBindingAdapter(attribute = "android:checked", event = "onChecked")
//    public static boolean isChecked(CompoundButton view) {
//        return view.isChecked();
//    }
//
//    /**
//     * when this event happen, inverseBinding will work.
//     *
//     * @param view           Spinner
//     * @param onItemSelected on user action.
//     */
//    @BindingAdapter("onChecked")
//    public static void setOnCheckedChanged(CompoundButton view, @NonNull final InverseBindingListener onItemSelected) {
//        view.setOnCheckedChangeListener((buttonView, isChecked) -> onItemSelected.onChange());
//    }
}
