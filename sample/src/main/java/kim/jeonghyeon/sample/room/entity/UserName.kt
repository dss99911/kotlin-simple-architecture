package kim.jeonghyeon.sample.room.entity

import androidx.room.ColumnInfo

data class UserName(val id: String,
                    val firstName: String,
                    @ColumnInfo(name = "last_name")
                    val lastName: String)