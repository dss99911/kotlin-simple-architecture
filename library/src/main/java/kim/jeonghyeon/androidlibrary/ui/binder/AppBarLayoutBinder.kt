package kim.jeonghyeon.androidlibrary.ui.binder

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout

@BindingAdapter("onCollapsingStatusChanged")
fun AppBarLayout.onCollapsingStatusChanged(listener: CollapsingStatusChangeListener) {
    addOnOffsetChangedListener(object :
        AppBarLayout.OnOffsetChangedListener {
        var status = CollapsingStatus.EXPANDED
        override fun onOffsetChanged(appbar: AppBarLayout, verticalOffset: Int) {
            val previousStatus = status
            status = when {
                Math.abs(verticalOffset) == appbar.totalScrollRange -> CollapsingStatus.COLLAPSED
                verticalOffset == 0 -> CollapsingStatus.EXPANDED
                else -> CollapsingStatus.MOVING
            }

            if (previousStatus == status) {
                return
            }
            listener.onCollapsingStatusChanged(status)
        }
    })
}

interface CollapsingStatusChangeListener {
    fun onCollapsingStatusChanged(status: CollapsingStatus)
}


enum class CollapsingStatus {
    COLLAPSED,
    EXPANDED,
    MOVING
}