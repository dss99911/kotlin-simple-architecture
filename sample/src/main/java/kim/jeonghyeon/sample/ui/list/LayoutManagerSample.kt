package kim.jeonghyeon.sample.ui.list

import androidx.recyclerview.widget.LinearLayoutManager

class LayoutManagerSample {
    fun getItemCount() {
        val layoutManager: LinearLayoutManager? = null
        val totalItemCount = layoutManager?.itemCount
        val visibleItemCount = layoutManager?.childCount
        val lastVisibleItem = layoutManager?.findLastVisibleItemPosition()
    }
}