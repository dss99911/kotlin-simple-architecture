package kim.jeonghyeon.androidlibrary.sample.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import kim.jeonghyeon.androidlibrary.sample.room.repository.UserRepository

object Injection {

    private fun provideUserRepository(context: Context): UserRepository {
        return UserRepository()
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideUserRepository(context))
    }

}