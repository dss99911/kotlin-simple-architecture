package kim.jeonghyeon.androidlibrary.architecture.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

@SuppressWarnings({"EmptyMethod", "unused"})
public class Presenter<U extends Ui> {
    @Nullable
    private U mUi;

    @Nullable
    public U getUi() {
        return mUi;
    }

    @Nullable
    public FragmentActivity getActivity() {
        U ui = getUi();
        if (ui == null) {
            return null;
        }
        return ui.getActivity();
    }

    public void finish() {
        U ui = getUi();
        if (ui == null) {
            return;
        }

        ui.finish();
    }

    @CallSuper
    public void onUiReady(U ui) {
        mUi = ui;
    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }




    @CallSuper
    public void onUiUnready(U ui) {
        mUi = null;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Nullable
    public String getString(@StringRes int resourceId) {
        if (getUi() == null) {
            return null;
        }
        Context context = getUi().getUiContext();
        if (context == null) {
            return null;
        }
        return context.getString(resourceId);
    }

    @Nullable
    public String getString(@StringRes int resourceId, Object... formatArgs) {
        if (getUi() == null) {
            return null;
        }
        Context context = getUi().getUiContext();
        if (context == null) {
            return null;
        }
        return context.getString(resourceId, formatArgs);
    }

}
