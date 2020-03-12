package kim.jeonghyeon.sample

import kim.jeonghyeon.sample.list.ListViewModel
import kim.jeonghyeon.sample.list.paging.PagingViewModel
import kim.jeonghyeon.sample.list.paging.api.GithubService
import kim.jeonghyeon.sample.list.radiobox.RadioBoxListViewModel
import kim.jeonghyeon.sample.list.simple.SimpleListViewModel
import kim.jeonghyeon.sample.list.simplecomparable.SimpleComparableListViewModel
import kim.jeonghyeon.sample.view.ViewViewModel
import kim.jeonghyeon.sample.view.menu.MenuViewModel
import kim.jeonghyeon.sample.viewmodel.ViewModelViewModel
import kim.jeonghyeon.sample.viewmodel.debounce.DebounceViewModel
import kim.jeonghyeon.sample.viewmodel.navargs.NavArgsFragmentArgs
import kim.jeonghyeon.sample.viewmodel.navargs.NavArgsViewModel
import kim.jeonghyeon.sample.viewmodel.otherview.OtherViewModelViewModel
import kim.jeonghyeon.sample.viewmodel.parent.ParentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainActivityViewModel() }
    viewModel { MainViewModel() }

    //list
    viewModel { ListViewModel() }
    viewModel { SimpleListViewModel() }
    viewModel { SimpleComparableListViewModel() }
    viewModel { PagingViewModel(get()) }
    viewModel { RadioBoxListViewModel() }

    //view
    viewModel { ViewViewModel() }
    viewModel { MenuViewModel() }

    //viewmodel
    viewModel { ViewModelViewModel() }
    viewModel { (args: NavArgsFragmentArgs) ->
        NavArgsViewModel(args)
    }
    viewModel { (parent: MainActivityViewModel) ->
        ParentViewModel(
            parent
        )
    }
    viewModel { DebounceViewModel() }
    viewModel { (mainViewModel: MainViewModel) -> OtherViewModelViewModel(mainViewModel) }


    //api
    factory { GithubService.create() }
}