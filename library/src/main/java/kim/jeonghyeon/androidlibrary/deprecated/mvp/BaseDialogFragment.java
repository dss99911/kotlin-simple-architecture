package kim.jeonghyeon.androidlibrary.deprecated.mvp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

@SuppressWarnings("unused")
public abstract class BaseDialogFragment<P extends Presenter<U>, U extends Ui> extends DialogFragment implements Ui {
    private final P mPresenter = createPresenter();

    public BaseDialogFragment() {
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

    @Override
    public Context getUiContext() {
        return getContext();
    }

    public void showWithTryCatch(@Nullable FragmentTransaction fragmentTransaction, String packageName) {
        if (fragmentTransaction == null) {
            return;
        }

        show(fragmentTransaction, packageName);
    }

    @Override
    public void finish() {
        dismiss();
    }
}
