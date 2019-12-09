@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.deprecated

import android.content.res.Resources
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.Surface
import kim.jeonghyeon.androidlibrary.extension.ctx
import org.jetbrains.anko.windowManager

@Suppress("MemberVisibilityCanBePrivate")
object Sty {
    private val displayMetrics by lazy { Resources.getSystem().displayMetrics }
    @JvmStatic
    fun getScreenWidth(): Int =
            if (displayMetrics.widthPixels < displayMetrics.heightPixels) displayMetrics.widthPixels
            else displayMetrics.heightPixels
    @JvmStatic
    fun getScreenHeight(): Int =// in Pixels
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) displayMetrics.widthPixels
            else displayMetrics.heightPixels
    @JvmStatic
    fun per2px(percent: Float): Int =
            if (percent == 0f) 0
            else (percent / 100 * getScreenWidth()).toInt()

    @JvmStatic
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    @JvmStatic
    fun dp2px(dp: Int): Int {
        return dp * displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT    // px
    }

    @JvmStatic
    fun px2dp(px: Int): Int {
        return px * DisplayMetrics.DENSITY_DEFAULT / displayMetrics.densityDpi    // dp
    }
}

fun PointF.changeCurrentRotationToRotation0(): PointF {

    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    var nextX = x
    var nextY = y
    when (ctx.windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_90 -> {
            //x = h - y
            //y = x
            nextX = height - y
            nextY = x

        }
        Surface.ROTATION_180 -> {
            //x = w - x
            //y = h - y
            nextX = width - x
            nextY = height - y
        }
        Surface.ROTATION_270 -> {
            //x = y
            //y = w - x
            nextX = y
            nextY = width - x
        }
    }

    x = nextX
    y = nextY
    return this
}

fun PointF.changeRotation0ToCurrentRotation(): PointF {
    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    var nextX = x
    var nextY = y
    when (ctx.windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_90 -> {
            //x = y
            //y = h - x
            nextX = y
            nextY = height - x

        }
        Surface.ROTATION_180 -> {
            //x = w - x
            //y = h - y
            nextX = width - x
            nextY = height - y
        }
        Surface.ROTATION_270 -> {
            //x = w - y
            //y = x
            nextX = width - y
            nextY = x
        }
    }

    x = nextX
    y = nextY
    return this
}