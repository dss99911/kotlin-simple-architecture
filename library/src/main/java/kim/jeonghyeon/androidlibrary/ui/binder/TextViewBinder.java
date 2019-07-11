package kim.jeonghyeon.androidlibrary.ui.binder;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;

import static kim.jeonghyeon.androidlibrary.extension.GlobalFunctionsKt.isFromVersion;

/**
 * Created by hyun.kim on 07/12/17.
 * <p>
 * about TextView Binding
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class TextViewBinder {
    /**
     * @param fonts R.font.gotham_book
     */
    @BindingAdapter(value = {"texts", "fonts", "textColors", "textSizes"}, requireAll = false)
    public static void bindTexts(@NonNull TextView textView, @Nullable String[] texts, int[] fonts, int[] colors, float[] pxSizes) {
        if (texts == null || texts.length == 0) {
            textView.setText(null);
            return;
        }

        setText(textView, texts, fonts, colors, pxSizes);
    }

    @SuppressLint("ObsoleteSdkInt")
    @BindingAdapter("android:drawableTint")
    public static void bindTexts(@NonNull TextView textView, int color) {
        if (isFromVersion(Build.VERSION_CODES.M)) {
            textView.setCompoundDrawableTintList(ColorStateList.valueOf(color));
        } else {
            Drawable[] drawables = textView.getCompoundDrawables();
            for (Drawable drawable : drawables) {
                if (drawable == null) {
                    continue;
                }
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @BindingAdapter("textStrikeThrough")
    public static void textStrikeThrough(@NonNull TextView textView, boolean enabled) {
        if (enabled) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public static void setText(@NonNull TextView tv, String[] texts, int[] fonts, Object colors, @Nullable float[] sizes) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (String txt : texts) sb.append(txt);
        int count = 0;
        for (int i = 0; i < texts.length; i++) {

            sb.setSpan(new CustomTypefaceSpan("", ResourcesCompat.getFont(tv.getContext(), fonts[i])), count, count + texts[i].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            if (sizes != null)
                sb.setSpan(new AbsoluteSizeSpan((int) sizes[i], false), count, count + texts[i].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            if (colors instanceof int[])
                sb.setSpan(new ForegroundColorSpan(((int[]) colors)[i]), count, count + texts[i].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            count += texts[i].length();
        }

        tv.setText(sb);

    }

    @BindingAdapter(value = {"drawableWidth", "drawableHeight"})
    public static void addCompoundImage(TextView tv, float width, float height) {
        Drawable[] compoundDrawables = tv.getCompoundDrawables();

        for (Drawable compoundDrawable : compoundDrawables) {
            if (compoundDrawable == null) {
                continue;
            }

            compoundDrawable.setBounds(0, 0, (int) width, (int) height);
        }
        tv.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

}

@SuppressWarnings({"unused", "WeakerAccess"})
class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;

    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        newType = type;
    }

    private static void applyCustomTypeFace(Paint paint, @NonNull Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }


    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }
}