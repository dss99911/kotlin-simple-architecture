package kim.jeonghyeon.sample.ui.resource

import android.content.Context

fun ResourceByString(context: Context) {
    context.resources.getIdentifier("icon_noti_lollipop", "drawable", context.packageName)
}