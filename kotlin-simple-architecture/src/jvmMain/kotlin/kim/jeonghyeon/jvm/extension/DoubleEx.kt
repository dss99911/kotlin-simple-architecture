@file:Suppress("unused")

package kim.jeonghyeon.jvm.extension

import java.math.RoundingMode
import java.text.DecimalFormat

//todo delete?
fun Double.ceil(decimalPlace: Int): Double {

    val df = DecimalFormat(
        if (decimalPlace == 0) "#"
        else "#." + ("#".repeat(decimalPlace))
    )
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}