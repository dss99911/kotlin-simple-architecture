package kim.jeonghyeon.sample.ui.list

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.BasePagedListAdapter
import kim.jeonghyeon.sample.ServiceLocator
import kim.jeonghyeon.sample.room.entity.User
import kim.jeonghyeon.sample.room.repository.UserRepository

class PagedListViewModelExample(userRepository: UserRepository = ServiceLocator.provideUserRepository()) : ViewModel(), BasePagedListAdapter.OnItemClickListener<User> {
    val pagedList: LiveData<PagedList<User>> = userRepository.allUserPaging

    override fun onItemClick(adapter: BasePagedListAdapter<User>, view: View, position: Int) {

    }
}