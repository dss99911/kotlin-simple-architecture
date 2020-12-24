package kim.jeonghyeon.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 * doesn't support multiple activity. only one backStack exists
 * because, android, ios method is different. and using only one activity for compose has no issue for most project.
 * TODO but some project need multiple backStack. so, analyze how to implement on IOS, for Android, maybe use Ambient.
 */
object Navigator {
    //TODO [KSA-140] support savedState on Android
    // approach is to save configured DataFlow or data.
    //TODO consider to use MutableStateFlow
    private val _backStack = MutableStateFlow<List<BaseViewModel>>(emptyList())

    val backStack: List<BaseViewModel> get() = _backStack.value
    val size: Int get() = backStack.size

    val currentFlow: Flow<BaseViewModel> = _backStack.map {
        it.lastOrNull()?: error("viewModel doesn't exist. at least one viewmoel should exist.")
    }

    /**
     * used on IOS
     */
    fun watchCurrent(scope: CoroutineScope, perform: (BaseViewModel) -> Unit) {
        scope.launch {
            currentFlow.collect {
                perform(it)
            }
        }
    }

    val current: BaseViewModel get() = backStack.lastOrNull()?: error("viewModel doesn't exist. at least one viewmoel should exist.")
    val previous: BaseViewModel? get() = if (backStack.size > 1) backStack[backStack.lastIndex -1] else null
    val root: BaseViewModel get() = backStack.first()

    //todo how to lock?
    /**
     * go back
     * @return the viewModel removed.
     */
    fun back(): BaseViewModel? {
        if (backStack.size <= 1) {
            return null
        }

        val list = backStack.toMutableList()

        return list.removeLast().apply {
            onBackPressed()
        }.also {
            _backStack.value = list
        }


    }

    /**
     * if same viewModel instance is added, it won't be added and return false
     * prevent to add same screen two times when user click button two times at the same time
     */
    fun navigate(viewModel: BaseViewModel): Boolean {
        if (backStack.contains(viewModel)) {
            return false
        }
        _backStack.value = backStack + viewModel
        return true
    }

    /**
     * different with [backUpToRoot]. this reset existing root viewModel and replace with [viewModel]
     */
    fun clearAndNavigate(viewModel: BaseViewModel) {
        backStack.forEach { it.onBackPressed() }
        _backStack.value = listOf(viewModel)
    }

    fun replace(viewModel: BaseViewModel) {
        val list = backStack.toMutableList().apply {
            removeLast().onBackPressed()
            add(viewModel)
        }
        _backStack.value = list
    }

    /**
     * pop until the [viewModel]
     * @param inclusive if true, remove the [viewModel] also
     * @return if viewModel not exists in the stack. return false
     */
    fun backUpTo(viewModel: BaseViewModel, inclusive: Boolean = false): Boolean {
        val viewModelIndex = backStack.lastIndexOf(viewModel).takeIf { it >= 0 } ?: return false
        val inclusivePopIndex = viewModelIndex + (if (inclusive) 0 else 1)

        if (inclusivePopIndex == backStack.size) return true//there is nothing to pop

        backStack.filterIndexed { index, viewModel ->
            if (index >= inclusivePopIndex) {
                viewModel.onBackPressed()
                false
            } else true
        }.let {
            _backStack.value = it
        }
        return true
    }

    fun backUpToRoot() {
        backUpTo(backStack.first(), false)
    }
}