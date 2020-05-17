package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

interface DiffComparable<T> {
    fun areItemsTheSame(item: T): Boolean
    fun areContentsTheSame(item: T): Boolean
}