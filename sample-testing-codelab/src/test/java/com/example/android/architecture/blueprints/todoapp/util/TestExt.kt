package com.example.android.architecture.blueprints.todoapp.util

import kotlinx.coroutines.test.runBlockingTest
import org.koin.core.qualifier.Qualifier
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <T, U> U.onCalling(methodCall: U.() -> T): OngoingStubbing<T> {
    return Mockito.`when`(methodCall())
}

@Suppress("EXPERIMENTAL_API_USAGE")
inline fun <reified T : Any> KoinTest.declareMockSuspend(
    qualifier: Qualifier? = null,
    noinline stubbing: suspend T.() -> Unit
): T =
    declareMock(qualifier) {
        runBlockingTest {
            stubbing.invoke(this@declareMock)
        }
    }