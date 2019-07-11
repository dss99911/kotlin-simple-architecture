package kim.jeonghyeon.androidlibrary.ui;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class LongToast implements Runnable {

    @NonNull
    private final Handler handler;
    private final Context context;
    private String text;
    private int count;
    private View view;
    private int gravity;
    private int xOffset;
    private int yOffset;

    public LongToast(Context context) {
        this.context = context;
        handler = new Handler();
    }

    @NonNull
    public static LongToast makeText(Context context, String text) {
        return new LongToast(context).withText(text);
    }

    @NonNull
    public static LongToast makeText(@NonNull Context context, int textResId) {
        return new LongToast(context).withText(context.getString(textResId));
    }

    /**
     * 1count 에 3초로 계산?
     */
    public void start(int count) {
        if (count <= 0) return;
        this.count = count;
        cancel();
        show();
    }

    public void show() {
        if (count <= 0) return;

        count--;

        if (text != null) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        } else if (view != null) {
            Toast toast = new Toast(context);
            toast.setView(view);
            if (gravity != 0) {
                toast.setGravity(gravity, xOffset, yOffset);
            }
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }


        handler.postDelayed(this, 3000);
    }

    public void cancel() {
        handler.removeCallbacks(this);
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.gravity = gravity;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @NonNull
    public LongToast withText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public void run() {
        show();
    }
}