package kim.jeonghyeon.androidlibrary.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Target

@Target(
    ElementType.TYPE,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.CONSTRUCTOR
)
annotation class KeepName