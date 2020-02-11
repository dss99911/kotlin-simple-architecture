package kim.jeonghyeon.sample.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kim.jeonghyeon.androidlibrary.architecture.repository.BaseRoomDatabase
import kim.jeonghyeon.androidlibrary.architecture.repository.Converters
import kim.jeonghyeon.androidlibrary.architecture.repository.createDatabase
import kim.jeonghyeon.sample.room.dao.UserDao
import kim.jeonghyeon.sample.room.entity.User


@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserDatabase : BaseRoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        val instance by lazy { createDatabase<UserDatabase>() }
    }

}

/**
 * inmomory database
 */
@Database(
        entities = [User::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RedditDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory: Boolean): RedditDb {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RedditDb::class.java)
            } else {
                Room.databaseBuilder(context, RedditDb::class.java, "reddit.db")
            }
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun posts(): UserDao
}