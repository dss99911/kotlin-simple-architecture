package kim.jeonghyeon.androidlibrary.compose.modifier

import androidx.compose.Composable
import androidx.ui.core.*

enum class Visibility {
    VISIBLE, INVISIBLE, GONE
}
@Composable
fun Modifier.visibility(visibility: Visibility) = this + VisibleModifier(visibility)

@Composable
fun Modifier.visible(visible: Boolean) = this + VisibleModifier(if (visible)Visibility.VISIBLE else Visibility.GONE)

private data class VisibleModifier(val visibility: Visibility) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): MeasureScope.MeasureResult {
        return if (visibility == Visibility.GONE) {
            layout(0, 0) {
                // Empty placement block
            }
        } else {
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                if (visibility == Visibility.VISIBLE) {
                    placeable.place(0, 0)
                }
            }
        }

    }
}
