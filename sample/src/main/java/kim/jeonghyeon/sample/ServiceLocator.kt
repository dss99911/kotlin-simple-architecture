package kim.jeonghyeon.sample

import androidx.annotation.VisibleForTesting
import kim.jeonghyeon.sample.room.database.UserDatabase
import kim.jeonghyeon.sample.room.repository.UserRepository

object ServiceLocator {

    private val lock = Any()
    private var database: UserDatabase? = null


    private var userRepository: UserRepository? = null

    fun provideUserRepository(): UserRepository {
        synchronized(this) {
            return userRepository ?:
            userRepository ?: createUserRepository()
        }
    }

    @VisibleForTesting
    fun setRepository(userRepository: UserRepository) {
        this.userRepository = userRepository
    }

    private fun createUserRepository(): UserRepository {
        return UserRepository(UserDatabase.instance.userDao())
    }
}