package kim.jeonghyeon.kotlinlibrary.extension

import java.math.BigDecimal

inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}