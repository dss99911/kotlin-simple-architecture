package kim.jeonghyeon.androidlibrary.sample.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable
import java.util.*

@Entity
data class User (
//        @PrimaryKey var uid: Int,
//        @PrimaryKey var id: String = UUID.randomUUID().toString() // UUID
    @PrimaryKey(autoGenerate = true) var id: Int,//autogenerate // AutoGenerate Int

//        @ColumnInfo(name = "first_name") var firstName: String?,
        @ColumnInfo var firstName: String?,//if not set, column name is same with field name
        @ColumnInfo(name = "last_name") var lastName: String?,
        var vip: Boolean,//able to omit the @ColumnInfo
        var createdAt: Date? = null//use TypeConverter
) : DiffComparable<User> {//use for paging adapter
        override fun areItemsTheSame(item: User) = id == item.id

        override fun areContentsTheSame(item: User) = this == item
}