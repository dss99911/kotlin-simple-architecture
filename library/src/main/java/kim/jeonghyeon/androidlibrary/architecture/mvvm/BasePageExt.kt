package kim.jeonghyeon.androidlibrary.architecture.mvvm

import kim.jeonghyeon.androidlibrary.BR
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

inline fun <reified V : BaseViewModel> IBaseUi.bindingViewModel(
    variableId: Int = BR.model,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<V> {
    return viewModel<V>(qualifier, parameters).also {
        viewModels[variableId] = it
    }
}