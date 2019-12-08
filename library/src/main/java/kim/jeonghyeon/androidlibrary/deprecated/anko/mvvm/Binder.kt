@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.deprecated.anko.mvvm

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import kim.jeonghyeon.kotlinlibrary.extension.toDouble
import kotlin.properties.Delegates

class Binder<T>(initValue: T? = null) {
    private val bound: MutableSet<(item: T?) -> Unit> = HashSet()
    private var item: T? by Delegates.observable(initValue) { _, old, new -> if (old != new) bound.forEach { it(new) } }
    fun bind(binding: (item: T?) -> Unit) {
        bound.add(binding)
        binding(item)

    }

    fun set(value: T?) {
        item = value
    }

    fun get(): T? = item

    private var refreshAction: () -> T? = { item }

    fun withRefreshAction(action: () -> T?): Binder<T> {
        refreshAction = action
        return this
    }

    fun refresh() {
        set(refreshAction())
    }

    fun unbind() {
        bound.clear()
    }

    fun toDouble(): Double = get()?.toString().toDouble(0.0)
}

class ListBinder<VM : ViewModel> {
    private val boundAdd: MutableSet<(item: VM) -> Unit> = HashSet()
    private val boundRemove: MutableSet<(index: Int) -> Unit> = HashSet()
    private val list = mutableListOf<VM>()

    fun add(value: VM) {
        list.add(value)
        boundAdd.forEach { it(value) }
    }

    fun remove(index: Int) {
        list.removeAt(index)
        boundRemove.forEach { it(index) }
    }

    fun bindAdd(binding: (item: VM) -> Unit) {
        boundAdd.add(binding)
        list.forEach(binding)
    }

    fun bindRemove(binding: (index: Int) -> Unit) {
        boundRemove.add(binding)
    }
}


fun <T> View.bind(binder: Binder<T>, binding: (item: T?) -> Unit) = binder.bind(binding)
fun <T> ViewModel.bind(binder: Binder<T>, binding: (item: T?) -> Unit) = binder.bind(binding)
fun <T> View.bind(vararg binder: Binder<T>, binding: (item: T?) -> Unit) = binder.forEach { it.bind(binding) }
fun <T : ViewModel> View.bindList(binder: ListBinder<T>, addBinding: (item: T) -> Unit, removeBinding: (index: Int) -> Unit) {
    binder.bindAdd(addBinding)
    binder.bindRemove(removeBinding)
}

fun <T> TextView.bindText(binder: Binder<T>) {
    binder.bind {
        if (text.toString() != it?.toString()) {
            text = it?.toString()
        }
    }
}

fun TextView.inverseBindText(binder: Binder<String>) {
    bindText(binder)

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binder.set(s?.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}