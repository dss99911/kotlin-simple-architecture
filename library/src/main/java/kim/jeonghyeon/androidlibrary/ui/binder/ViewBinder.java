package kim.jeonghyeon.androidlibrary.ui.binder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.ViewCompat;
import androidx.databinding.BindingAdapter;

/**
 * Created by hyun.kim on 11/12/17.
 * BindingAdapter for View
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ViewBinder {
    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_below")
    public static void setLayoutBelow(View view, int id) {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        layoutParams.addRule(RelativeLayout.BELOW, id);
    }

    @BindingAdapter("android:layout_marginRight")
    public static void setMarginRight(View view, float rightMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }

        ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin = (int) rightMargin;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginLeft")
    public static void setMarginLeft(View view, float leftMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }

        ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin = (int) leftMargin;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(View view, float topMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }

        ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = (int) topMargin;
        view.setLayoutParams(layoutParams);
    }

    // to set background image in view
    @BindingAdapter("android:backgroundId")
    public static void setBackgroundImage(View view, @DrawableRes int resId) {
        view.setBackgroundResource(resId);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter(value = {"popupMenuId", "onPopupMenuClick"})
    public static void setPopupMenu(View view, int popupMenuId, PopupMenu.OnMenuItemClickListener onPopupMenuClick) {
        view.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), v);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(popupMenuId, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(onPopupMenuClick);
            popupMenu.show();
        });
    }

    @BindingAdapter(value = {"popupMenuId", "onPopupMenuLongClick"})
    public static void setPopupMenuLong(View view, int popupMenuId, PopupMenu.OnMenuItemClickListener onPopupMenuClick) {
        view.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), v);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(popupMenuId, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(onPopupMenuClick);
            popupMenu.show();
            return true;
        });
    }

    @BindingAdapter("android:backgroundTint")
    public static void setImageTint(ImageView view, @ColorInt int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }
}
