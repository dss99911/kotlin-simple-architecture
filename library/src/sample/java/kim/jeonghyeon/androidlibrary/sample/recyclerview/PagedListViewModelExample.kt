package kim.jeonghyeon.androidlibrary.sample.recyclerview

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import kim.jeonghyeon.androidlibrary.sample.room.entity.User
import kim.jeonghyeon.androidlibrary.sample.room.repository.UserRepository
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BasePagedListAdapter

class PagedListViewModelExample : ViewModel(), BasePagedListAdapter.OnItemClickListener<User> {
    val pagedList: LiveData<PagedList<User>> = UserRepository().allUserPaging

    override fun onItemClick(adapter: BasePagedListAdapter<User>, view: View, position: Int) {

    }
}