@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

import java.math.RoundingMode
import java.text.DecimalFormat

fun Double.ceil(decimalPlace: Int): Double {

    val df = DecimalFormat(
        if (decimalPlace == 0) "#"
        else "#." + ("#".repeat(decimalPlace))
    )
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}