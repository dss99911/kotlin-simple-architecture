package kim.jeonghyeon.androidlibrary.testutil

import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing



inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
inline fun <reified T> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)
fun <T> on(type: T): OngoingStubbing<T> = Mockito.`when`(type)