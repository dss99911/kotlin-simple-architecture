package kim.jeonghyeon.androidlibrary.architecture.repository

import androidx.room.Room
import androidx.room.RoomDatabase
import kim.jeonghyeon.androidlibrary.BuildConfig
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

        if (BuildConfig.isMock) {
            //TODO HYUN : check if this way is fine. reason : when run app with mock. database is kept. but mock api data is destroyed.
            return Room.inMemoryDatabaseBuilder(
                ctx,
                T::class.java
            ).build()
        }

        return Room.databaseBuilder(ctx, T::class.java, name).build()
    }
}