package kim.jeonghyeon.androidlibrary.ui.binder

import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("android:inputAllCaps")
fun EditText.setImageResource(allCaps: Boolean) {
    keyListener =
        DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
    filters += InputFilter.AllCaps()
}