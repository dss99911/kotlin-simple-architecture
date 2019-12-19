package kim.jeonghyeon.sample

import androidx.annotation.VisibleForTesting
import kim.jeonghyeon.sample.room.database.UserDatabase
import kim.jeonghyeon.sample.room.repository.UserRepository

object ServiceLocator {

    private var mockUserRepository: UserRepository? = null

    fun provideUserRepository(): UserRepository = mockUserRepository ?:UserRepository(UserDatabase.instance.userDao())

    @VisibleForTesting
    fun setRepository(userRepository: UserRepository) {
        this.mockUserRepository = userRepository
    }
}