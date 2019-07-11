package kim.jeonghyeon.androidlibrary.sample.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kim.jeonghyeon.androidlibrary.sample.room.entity.User

/**
 * if source livedata is changed, transformated livedata also receive the event
 */
class LiveDataTransformation {

    fun map() {
        val userLiveData: LiveData<User> = MutableLiveData<User>()

        val userName: LiveData<String> = Transformations.map(userLiveData) { user ->
            "${user.firstName} ${user.lastName}"
        }
    }

    /**
     * unlike map, switchMap change the liveData
     */
    fun switchMap() {
        fun getUser(id: String): MutableLiveData<User> = MutableLiveData()

        val userId: LiveData<String> = MutableLiveData<String>()

        val user = Transformations.switchMap(userId) { id -> getUser(id) }
    }


}