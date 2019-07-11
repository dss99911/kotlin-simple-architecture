package kim.jeonghyeon.androidlibrary.sample.room.repository

import androidx.paging.Config
import androidx.paging.toLiveData
import kim.jeonghyeon.androidlibrary.sample.room.database.UserDatabase
import kim.jeonghyeon.androidlibrary.sample.room.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class UserRepository {
    private val userDatabase = UserDatabase.instance
    private val userDao = userDatabase.userDao()
    private val allUsers = userDao.getAllSynced()
    val allUserPaging = userDao.getAllPaging()
        .toLiveData(Config(pageSize = 30,maxSize = 200))

    fun insert(user: User) {
        GlobalScope.async(Dispatchers.IO) {
            userDao.insert(user)
        }

    }
}