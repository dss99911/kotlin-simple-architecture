package kim.jeonghyeon.sample

import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.BaseApplication
import kim.jeonghyeon.sample.feature.apicall.ApiCallViewModel
import kim.jeonghyeon.sample.mvvm.NavigationFragmentArgs
import kim.jeonghyeon.sample.mvvm.NavigationViewModel
import kim.jeonghyeon.sample.mvvm.SampleMVVMViewModel
import kim.jeonghyeon.sample.mvvm.SampleParentViewModel
import kim.jeonghyeon.sample.retrofit.api.GithubService
import kim.jeonghyeon.sample.room.database.UserDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class SampleApplication : BaseApplication() {
    override fun getKoinModules() = listOf(appModule)
}

val appModule = module {

    viewModel { (handle: SavedStateHandle) -> MainActivityViewModel(handle) }
    viewModel { (handle: SavedStateHandle) -> SampleMVVMViewModel(handle) }
    viewModel { (args: NavigationFragmentArgs, handle: SavedStateHandle) ->
        NavigationViewModel(
            args,
            handle
        )
    }
    viewModel { MainViewModel() }
    viewModel { ApiCallViewModel(get(), get()) }
    viewModel { (parent: MainActivityViewModel) -> SampleParentViewModel(parent) }

    factory { GithubService.create() }
    factory { UserDatabase.instance.userDao() }
}