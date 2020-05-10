//package kim.jeonghyeon.androidlibrary.architecture.repository
//
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Update
//
//interface BaseDao<T> {
//    @Insert
//    suspend fun insert(vararg obj: T)
//    @Insert
//    suspend fun insert(obj: List<T>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun replace(obj: T)
//
//    @Delete
//    suspend fun delete(obj: T)
//
//    @Update
//    suspend fun update(obj: T)
//}