package kim.jeonghyeon.androidtesting

import org.mockito.ArgumentCaptor

inline fun <reified T> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)

inline fun <reified T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()