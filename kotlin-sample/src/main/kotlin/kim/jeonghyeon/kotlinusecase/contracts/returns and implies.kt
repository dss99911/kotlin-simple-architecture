package kim.jeonghyeon.kotlinusecase.contracts

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
fun Any?.isAValidName(): Boolean {
    contract {
        returns(true) implies (this@isAValidName is String)
    }
    return this != null && this is String && this.length > 3
}

@ExperimentalContracts
fun a(request: String?) {
    contract {
        returns() implies (request != null)
    }
}

@ExperimentalContracts
fun b(request: Any?): String? {
    contract {
        returnsNotNull() implies (request is String)
    }

    return "sadf"
}

fun getName(): String? {
    return "asdf"
}

@ExperimentalContracts
fun testUserName() {
    val name = getName()

    if (name.isAValidName()) {
        // No compile error! The compiler knows that because we told it with `implies`.
        val fullName: String = name
    }
}