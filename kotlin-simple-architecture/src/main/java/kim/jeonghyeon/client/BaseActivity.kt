package kim.jeonghyeon.client

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    abstract val rootViewModel: BaseViewModel

    /**
     * Activity's launch mode is singleInstance : one task, one activity only
     * Screen's launch mode is singleTop : if the screen is on top, just send 'onDeeplinkReceived' event
     */
    open val deeplinks: List<Deeplink> = emptyList()

    val currentViewModel: DataFlow<BaseViewModel> get() = Navigator.currentFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //when I test with Jetsurvey sample code
        //snackbar is shown above keyboard without setting this.
        //but, it's not working in this project, so, added here, as I think this should be default behavior
        //If there is way it's working without this, this can be removable
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        DeeplinkNavigator.deeplinks = deeplinks

        Navigator.navigate(rootViewModel)

        intent?.dataString?.let {
            DeeplinkNavigator.navigateToDeeplinkFromExternal(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.dataString?.let {
            DeeplinkNavigator.navigateToDeeplinkFromExternal(it)
        }
    }

    override fun onBackPressed() {
        if (currentViewModel.value?.canGoBack?.value == false) {
            return
        }
        Navigator.back() ?: super.onBackPressed()
    }
}