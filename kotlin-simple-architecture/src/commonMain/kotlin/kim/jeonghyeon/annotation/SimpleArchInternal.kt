package kim.jeonghyeon.annotation

/**
 * annotated function or class is used only in simple architecture. so, don't use
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING,
    message = "This Api can change"
)
annotation class SimpleArchInternal(val explanation: String =  "")