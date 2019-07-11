package kim.jeonghyeon.androidlibrary.architecture.repository

import androidx.room.*

interface BaseDao<T> {
    @Insert
    fun insert(vararg obj: T)
    @Insert
    fun insert(obj: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun replace(vararg obj: T)

    @Delete
    fun delete(obj: T)

    @Update
    fun update(obj: T)
}