package kim.jeonghyeon.sample.viewmodel.startactivity

import android.content.Context
import android.content.Intent
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseActivity
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

class StartActivityActivity : BaseActivity() {
    override val layoutId: Int = R.layout.activity_start_activity

    val viewModel by bindingViewModel<StartActivityActivityViewModel> {
        parametersOf(intent.getStringExtra(EXTRA_TEXT))
    }

    companion object {
        private val EXTRA_TEXT = "EXTRA_TEXT"
        fun getStartIntent(context: Context, text: String): Intent =
            Intent(context, StartActivityActivity::class.java).apply {
                putExtra(EXTRA_TEXT, text)
            }
    }
}