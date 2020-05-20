package kim.jeonghyeon.androidlibrary.architecture.repository//package kim.jeonghyeon.androidlibrary.architecture.repository
//
//import androidx.room.Room
//import kim.jeonghyeon.androidlibrary.architecture.repository.BaseRoomDatabase
//import kim.jeonghyeon.androidlibrary.extension.app
//import kim.jeonghyeon.androidlibrary.extension.ctx
//import kim.jeonghyeon.androidlibrary.extension.isTesting
//
//
//inline fun <reified T : BaseRoomDatabase> createDatabase(name: String = T::class.simpleName!!): T {
//    if (isTesting) {
//        return Room.inMemoryDatabaseBuilder(
//            ctx,
//            T::class.java
//        ).allowMainThreadQueries().build()
//    }
//
//    if (app.isMock) {
//        //TODO HYUN : check if this way is fine. reason : when run app with mock. database is kept. but mock api data is destroyed.
//        return Room.inMemoryDatabaseBuilder(
//            ctx,
//            T::class.java
//        ).build()
//    }
//
//    return Room.databaseBuilder(ctx, T::class.java, name).build()
//}