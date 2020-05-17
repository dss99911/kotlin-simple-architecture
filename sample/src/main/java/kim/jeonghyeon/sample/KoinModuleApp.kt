package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.net.api
import kim.jeonghyeon.androidlibrary.architecture.net.apiBuilder
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.apicall.callback.CallbackApi
import kim.jeonghyeon.sample.apicall.callback.CallbackApiCallViewModel
import kim.jeonghyeon.sample.apicall.chaining.ChainingApi
import kim.jeonghyeon.sample.apicall.chaining.ChainingApiCallViewModel
import kim.jeonghyeon.sample.apicall.chaining.LiveDataCallAdapterFactory
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApiCallViewModel
import kim.jeonghyeon.sample.apicall.debounce.DebounceApiCallViewModel
import kim.jeonghyeon.sample.apicall.parallel.ParallelApiCallViewModel
import kim.jeonghyeon.sample.apicall.polling.PollingApiCallViewModel
import kim.jeonghyeon.sample.apicall.reactive.ReactiveApi
import kim.jeonghyeon.sample.apicall.reactive.ReactiveApiCallViewModel
import kim.jeonghyeon.sample.apicall.retry.RetryApiCallViewModel
import kim.jeonghyeon.sample.apicall.simple.SimpleApiCallViewModel
import kim.jeonghyeon.sample.apicall.threading.ThreadingApi
import kim.jeonghyeon.sample.apicall.threading.ThreadingApiCallViewModel
import kim.jeonghyeon.sample.apicall.threading.ThreadingCallAdapterFactory
import kim.jeonghyeon.sample.db.simple.SimpleDbViewModel
import kim.jeonghyeon.sample.list.ListViewModel
import kim.jeonghyeon.sample.list.paging.PagingViewModel
import kim.jeonghyeon.sample.list.paging.api.GithubService
import kim.jeonghyeon.sample.list.radiobox.RadioBoxListViewModel
import kim.jeonghyeon.sample.list.simple.SimpleListViewModel
import kim.jeonghyeon.sample.list.simplecomparable.SimpleComparableListViewModel
import kim.jeonghyeon.sample.view.ViewViewModel
import kim.jeonghyeon.sample.view.menu.MenuViewModel
import kim.jeonghyeon.sample.view.parcelable.ParcelableViewModel
import kim.jeonghyeon.sample.view.savedState.SavedStateViewModel
import kim.jeonghyeon.sample.viewmodel.ViewModelViewModel
import kim.jeonghyeon.sample.viewmodel.navargs.NavArgsViewModel
import kim.jeonghyeon.sample.viewmodel.otherview.OtherViewModelViewModel
import kim.jeonghyeon.sample.viewmodel.parent.ParentViewModel
import kim.jeonghyeon.sample.viewmodel.permission.PermissionViewModel
import kim.jeonghyeon.sample.viewmodel.startactivity.StartActivityActivityViewModel
import kim.jeonghyeon.sample.viewmodel.startactivity.StartActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

val appModule = module {
    viewModel { MainActivityViewModel() }
    viewModel { MainViewModel() }

    //list
    viewModel { ListViewModel() }
    viewModel { SimpleListViewModel() }
    viewModel { SimpleComparableListViewModel() }
    viewModel { PagingViewModel(get()) }
    viewModel { RadioBoxListViewModel() }
    factory { GithubService.create() }

    //view
    viewModel { ViewViewModel() }
    viewModel { MenuViewModel() }
    viewModel { SavedStateViewModel(it[0]) }
    viewModel { ParcelableViewModel(it[0]) }

    //ui

    //viewmodel
    viewModel { ViewModelViewModel() }
    viewModel { NavArgsViewModel(it[0]) }
    viewModel { ParentViewModel(it[0]) }
    viewModel { OtherViewModelViewModel(it[0]) }
    viewModel { StartActivityViewModel(get()) }
    viewModel { PermissionViewModel() }
    viewModel { StartActivityActivityViewModel(it[0]) }

    //api call
    viewModel { ThreadingApiCallViewModel(get()) }
    viewModel { CallbackApiCallViewModel(get()) }
    viewModel { ChainingApiCallViewModel(get()) }
    viewModel { ReactiveApiCallViewModel(get()) }
    viewModel { CoroutineApiCallViewModel(get()) }
    viewModel { ParallelApiCallViewModel(get()) }
    viewModel { PollingApiCallViewModel(get()) }
    viewModel { DebounceApiCallViewModel(get()) }
    viewModel { RetryApiCallViewModel(get()) }
    viewModel { SimpleApiCallViewModel(get()) }
    factory {
        apiBuilder("http://demo7661478.mockable.io/")
            .addCallAdapterFactory(ThreadingCallAdapterFactory())
            .build()
            .create(ThreadingApi::class.java)
    }
    factory { api<CallbackApi>("http://demo7661478.mockable.io/") }
    factory {
        apiBuilder("http://demo7661478.mockable.io/")
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ChainingApi::class.java)
    }
    factory {
        apiBuilder("http://demo7661478.mockable.io/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ReactiveApi::class.java)
    }
    factory { api<CoroutineApi>("http://demo7661478.mockable.io/") }

    //change to your server address.
    factory { kim.jeonghyeon.common.net.api<SimpleApi>("http://192.168.1.26:8080") }

    //db
    viewModel { SimpleDbViewModel() }
}