package kim.jeonghyeon.androidlibrary.ui.widget;

import android.content.Context;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

public class AutoResizeSingleLineTextView extends AppCompatTextView {

    public AutoResizeSingleLineTextView(Context context) {
        super(context);
        setSingleLine();

    }

    /**
     * When text changes, set the force resize flag to true and reset the text size.
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        post(new Runnable() {
            @Override
            public void run() {
                resize();
            }
        });
    }

    private void resize() {
        for (int i = 0; i < 100; i++) {
            float txtWidth = getPaint().measureText(getText().toString());
            if (getRealWidth() >= txtWidth) break;
            setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 1);
        }


    }

    private int getRealWidth() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }
}