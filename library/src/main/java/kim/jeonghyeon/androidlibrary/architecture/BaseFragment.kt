package kim.jeonghyeon.androidlibrary.architecture

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import kim.jeonghyeon.androidlibrary.extension.log
import kim.jeonghyeon.androidlibrary.permission.PermissionFragment
import java.util.concurrent.atomic.AtomicInteger
interface IBaseFragment {
    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
    fun addFragment(container: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null)
}
open class BaseFragment : PermissionFragment(), IBaseFragment {

    private val nextRequestCode = AtomicInteger(1)
    private val resultListeners = SparseArray<(resultCode: Int, data: Intent?) -> Unit>()

    /**
     * used on pager. if not used always true.
     */
    var selected = true
        set(value) {
            field = value
            visible = isVisible(value, lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        }

    var visible = false
        private set(value) {
            if (value != field) {
                onVisibilityChanged(value)
            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName}")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        log("${this::class.simpleName}")
    }

    override fun onStart() {
        super.onStart()

        visible = isVisible(selected, true)
    }

    override fun onResume() {
        super.onResume()
        log("${this::class.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        log("${this::class.simpleName}")
    }

    override fun onStop() {
        super.onStop()

        visible = isVisible(selected, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName}")
    }

    private fun isVisible(selected: Boolean, isStarted: Boolean): Boolean = selected && isStarted

    open fun onVisibilityChanged(visible: Boolean) {
        log("${this::class.simpleName} : $visible")
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun addFragment(container: Int, fragment: Fragment, tag: String?) {
        (activity as? BaseActivity)?.addFragment(container, fragment, tag)
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun replaceFragment(container: Int, fragment: Fragment, tag: String?) {
        (activity as? BaseActivity)?.replaceFragment(container, fragment, tag)
    }

    override fun startActivityForResult(
        intent: Intent,
        onResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        val requestCode = nextRequestCode.getAndIncrement()
        resultListeners.put(requestCode, onResult)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultListeners[requestCode]?.invoke(resultCode, data)
        resultListeners.remove(requestCode)
    }
}