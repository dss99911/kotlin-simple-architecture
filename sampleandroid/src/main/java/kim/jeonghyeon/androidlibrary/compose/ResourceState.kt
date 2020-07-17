package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.MutableState
import androidx.compose.mutableStateOf
import kim.jeonghyeon.type.Resource

typealias ResourceState<T> = MutableState<Resource<T>>

fun <T> resourceStateOf(): ResourceState<T> = mutableStateOf(Resource.Start)

val <T> ResourceState<T>.isSuccess: Boolean get() = value.isSuccess()

fun <T> ResourceState<T>.data(): T = value.data()
fun <T> ResourceState<T>.dataOrNull(): T? = value.dataOrNull()
fun <T> ResourceState<T>.setSuccess(data: T) {
    value = Resource.Success(data)
}