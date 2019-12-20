package kim.jeonghyeon.androidlibrary.ui.binder

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import kim.jeonghyeon.androidlibrary.architecture.mvvm.Event

@Suppress("UNUSED_PARAMETER")
@BindingAdapter("clickEvent")
fun getClickEvent(view: View, event: Event<Unit>?) {

}

@Suppress("UNUSED_PARAMETER")
@InverseBindingAdapter(attribute = "clickEvent", event = "onClickEvent")
fun setClickEvent(view: View): Event<Unit> {
    return Event(Unit)
}

@BindingAdapter("onClickEvent")
fun setOnClickEvent(view: View, listener: InverseBindingListener)  {
    view.setOnClickListener { listener.onChange() }
}