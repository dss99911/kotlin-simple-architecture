package kim.jeonghyeon.kotlinusecase.contracts

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * use case
 * - local field initialization as below
 */
@ExperimentalContracts
fun createOnce(runFunction: () -> Unit) {
    contract {
        callsInPlace(runFunction, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    runFunction()
}

@ExperimentalContracts
fun getKotlinVersion(): Float {
    val kotlinVersion: Float
    createOnce {
        kotlinVersion = 1.3f
    }
    // no compile error!
    return kotlinVersion
}