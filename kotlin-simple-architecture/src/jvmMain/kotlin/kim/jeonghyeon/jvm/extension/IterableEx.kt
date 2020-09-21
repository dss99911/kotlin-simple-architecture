package kim.jeonghyeon.jvm.extension

import java.math.BigDecimal

//todo delete?
inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}