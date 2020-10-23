package kim.jeonghyeon.androidlibrary.compose

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ColumnScope.Companion.align
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.RowScope.Companion.align
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * this just call Modifier functions.
 * the reason to add here same function again,
 * is that we have to add Modifier keyword always and also need to import. importing also takes time.
 */
object ScreenUtil {
    fun RowScope.gravity(align: Alignment.Vertical): Modifier = Modifier.align(align)
    fun ColumnScope.gravity(align: Alignment.Horizontal): Modifier = Modifier.align(align)

    fun ColumnScope.weight(
        @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
        fill: Boolean = true
    ): Modifier =
        Modifier.weight(weight, fill)

    fun RowScope.weight(
        @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
        fill: Boolean = true
    ): Modifier =
        Modifier.weight(weight, fill)

    fun padding(all: Dp) = Modifier.padding(all)

    inline val Int.dp: Dp get() = Dp(value = this.toFloat())
}