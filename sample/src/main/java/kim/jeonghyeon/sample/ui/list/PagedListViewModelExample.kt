package kim.jeonghyeon.sample.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import kim.jeonghyeon.sample.ServiceLocator
import kim.jeonghyeon.sample.room.entity.User
import kim.jeonghyeon.sample.room.repository.UserRepository

class PagedListViewModelExample(userRepository: UserRepository = ServiceLocator.provideUserRepository()) : ViewModel() {
    val pagedList: LiveData<PagedList<User>> = userRepository.allUserPaging

}