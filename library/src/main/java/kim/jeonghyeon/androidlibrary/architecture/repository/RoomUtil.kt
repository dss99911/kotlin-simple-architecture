package kim.jeonghyeon.androidlibrary.architecture.repository

import androidx.room.Room
import androidx.room.RoomDatabase
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.isTesting

object RoomUtil {
    /**
     * make sure that name doesn't shrink by proguard
     */
    inline fun <reified T : RoomDatabase> getDatabase(name: String = T::class.simpleName!!): T {
        if (isTesting) {
            return Room.inMemoryDatabaseBuilder(
                ctx,
                T::class.java
            ).allowMainThreadQueries().build()
        }
        return Room.databaseBuilder(ctx, T::class.java, name).build()
    }
}