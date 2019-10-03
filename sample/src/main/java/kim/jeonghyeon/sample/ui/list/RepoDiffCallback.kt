package kim.jeonghyeon.sample.ui.list

import androidx.recyclerview.widget.DiffUtil
import kim.jeonghyeon.androidlibrary.sample.Repo

class RepoDiffCallback(
    private val mOldItems: List<Repo>,
    private val mNewItems: List<Repo>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldItems.size
    }

    override fun getNewListSize(): Int {
        return mNewItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return compare(oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return compare(oldItemPosition, newItemPosition)
    }

    private fun compare(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldValue = mOldItems.getOrElse(oldItemPosition) { return false }
        val newValue = mNewItems.getOrElse(newItemPosition) { return false }

        return oldValue == newValue
    }
}
