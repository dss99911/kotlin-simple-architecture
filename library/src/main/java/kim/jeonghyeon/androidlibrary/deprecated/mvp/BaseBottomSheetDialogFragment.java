package kim.jeonghyeon.androidlibrary.deprecated.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static kim.jeonghyeon.androidlibrary.extension.GlobalFunctionsKt.isFromVersion;

@SuppressWarnings("unused")
public abstract class BaseBottomSheetDialogFragment<P extends Presenter<U>, U extends Ui>
        extends BottomSheetDialogFragment implements Ui {

    private final P mPresenter = createPresenter();

    public BaseBottomSheetDialogFragment() {
        super();
    }

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected abstract U getUi();

    @NonNull
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onUiReady(getUi());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onUiUnready(getUi());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public Context getUiContext() throws NullPointerException {
        if (isFromVersion(Build.VERSION_CODES.M)) {
            return getContext();
        } else {
            Activity activity = getActivity();
            if (activity == null) {
                return null;
            }
            return activity.getBaseContext();
        }
    }

    public void showWithTryCatch(@Nullable FragmentTransaction fragmentTransaction, String packageName) {
        if (fragmentTransaction == null) {
            return;
        }

        super.show(fragmentTransaction, packageName);
    }

    @Override
    public void finish() {
        dismiss();
    }

}
