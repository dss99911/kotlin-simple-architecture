package kim.jeonghyeon.sample.room.repository

import androidx.paging.Config
import androidx.paging.toLiveData
import kim.jeonghyeon.sample.room.dao.UserDao
import kim.jeonghyeon.sample.room.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class UserRepository(private val userDao: UserDao) {
    private val allUsers = userDao.getAllSynced()
    val allUserPaging = userDao.getAllPaging()
        .toLiveData(Config(pageSize = 30,maxSize = 200))

    fun insert(user: User) {
        GlobalScope.async(Dispatchers.IO) {
            userDao.insert(user)
        }

    }
}