package kim.jeonghyeon.androidlibrary.ui.binder

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

// method to set drawable to databinding imageView attribute
@BindingAdapter("android:srcId")
fun setImageResource(view: ImageView, @DrawableRes resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter("android:srcBitmap")
fun setImageBitmap(view: ImageView, bm: Bitmap?) {
    view.setImageBitmap(bm)
}

@BindingAdapter("android:tint")
fun ImageView.setImageTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

@BindingAdapter("imgUrl")
fun ImageView.setImageUrl(imgUrl: String) {
    if (!TextUtils.isEmpty(imgUrl)) {
        Picasso.get()
            .load(imgUrl)
            .fit()
            .into(this)
    }
}

@BindingAdapter("imgUrl", "defaultImg")
fun ImageView.setImageUrl(imgUrl: String, defaultImg: Drawable) {
    if (!TextUtils.isEmpty(imgUrl)) {
        Picasso.get()
            .load(imgUrl)
            .placeholder(defaultImg)
            .error(defaultImg)
            .into(this)
    } else {
        setImageDrawable(defaultImg)
    }
}

@BindingAdapter("imgUrl", "defaultImg", "errorImg")
fun ImageView.setImageUrl(imgUrl: String, defaultImg: Drawable, errorImg: Drawable) {
    if (!TextUtils.isEmpty(imgUrl)) {
        Picasso.get()
            .load(imgUrl)
            .placeholder(defaultImg)
            .error(errorImg)
            .into(this)
    } else {
        setImageDrawable(defaultImg)
    }
}