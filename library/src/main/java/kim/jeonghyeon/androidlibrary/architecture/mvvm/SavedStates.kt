package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.asLiveObject
import kim.jeonghyeon.androidlibrary.extension.app
import kotlin.reflect.KProperty

fun IBaseUi.getSavedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): SavedStateHandle {
    return SavedStateViewModelFactory(
        app,
        savedStateRegistryOwner
    ).create(SavedStateViewModel::class.java)
        .savedStateHandle
}

fun IBaseUi.savedState(savedStateRegistryOwner: SavedStateRegistryOwner = this): Lazy<SavedStateHandle> {
    return lazy { getSavedState(savedStateRegistryOwner) }
}

inline fun <reified T> IBaseUi.savedStateDelegate(defaultValue: T) =
    SavedStateHandleDelegate(savedState, null, defaultValue)

inline fun <reified T> IBaseUi.savedStateDelegate(
    savedStateHandle: SavedStateHandle = savedState,
    defaultValue: T
) =
    SavedStateHandleDelegate(savedStateHandle, null, defaultValue)

inline fun <reified T> BaseViewModel.savedStateDelegate(defaultValue: T) =
    SavedStateHandleDelegate(savedStateHandle!!, null, defaultValue)

inline fun <reified T> BaseViewModel.savedStateDelegate(key: String, defaultValue: T) =
    SavedStateHandleDelegate(savedStateHandle!!, key, defaultValue)

inline fun <reified T> BaseViewModel.savedStateLiveData(key: String? = null) =
    SavedStateHandleLiveDataDelegate<T>(savedStateHandle!!, key)

class SavedStateHandleDelegate<T>(
    private val savedStateHandle: SavedStateHandle,
    val key: String? = null,
    val defaultValue: T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        savedStateHandle[key ?: property.name] ?: defaultValue

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        savedStateHandle[key ?: property.name] = value
    }
}

class SavedStateHandleLiveDataDelegate<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String? = null
) {
    lateinit var value: LiveObject<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): LiveObject<T> {
        if (!::value.isInitialized) {
            value = savedStateHandle.getLiveData<T>(key ?: property.name).asLiveObject()
        }
        return value
    }


}

internal class SavedStateViewModel(val savedStateHandle: SavedStateHandle) : ViewModel()