package kim.jeonghyeon.sample.room.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kim.jeonghyeon.androidlibrary.architecture.repository.BaseDao
import kim.jeonghyeon.sample.room.entity.User
import kim.jeonghyeon.sample.room.entity.UserName

@Dao
interface UserDao : BaseDao<User> {
    @Query("SELECT * FROM User")
    fun getAllSynced(): LiveData<List<User>>

    /**
     * this is not synced. because it is just getting data.
     * on one time task, it will be useful
     */
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM User where vip is 1")
    fun getVIPs(): LiveData<List<User>>//false : 0, true : 1

    /**
     * get data by paging
     */
    @Query("SELECT * FROM User")
    fun getAllPaging(): DataSource.Factory<Int, User>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE firstName LIKE :first AND last_name LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): User

    @Query("SELECT id, firstName, last_name FROM User")
    fun getUsersMinimal(): List<UserName>

    @Query(
            """
        SELECT * FROM user
        WHERE firstName = :name
        ORDER BY firstName DESC"""
    )
    fun loadUsers(name: String): LiveData<List<User>>

    @Query("delete from user")
    fun deleteAll()

    @Transaction
    fun clearAndInsert(users: List<User>) {
        deleteAll()
        insert(users)
    }

}