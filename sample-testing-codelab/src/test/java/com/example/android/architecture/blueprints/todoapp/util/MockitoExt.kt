package com.example.android.architecture.blueprints.todoapp.util

import com.google.common.truth.Truth.assertThat
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing


inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
inline fun <reified T> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)
fun <T> on(type: T): OngoingStubbing<T> = Mockito.`when`(type)

fun assertNotReachable() {
    assertThat(false).isTrue()
}

inline fun <reified T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()