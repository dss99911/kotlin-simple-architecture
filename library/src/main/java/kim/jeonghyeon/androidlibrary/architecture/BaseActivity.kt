package kim.jeonghyeon.androidlibrary.architecture

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import kim.jeonghyeon.androidlibrary.extension.log
import kim.jeonghyeon.androidlibrary.permission.PermissionActivity
import java.util.concurrent.atomic.AtomicInteger

interface IBaseActivity {
    fun addFragment(container: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null)

    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
}

open class BaseActivity : PermissionActivity(), IBaseActivity {

    private val nextRequestCode = AtomicInteger(1)
    private val resultListeners = SparseArray<(resultCode: Int, data: Intent?) -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName} onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName} onDestroy")
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun addFragment(container: Int, fragment: Fragment, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.add(container, fragment)
        } else {
            transaction.add(container, fragment, tag)
        }
        transaction.commitNow()
    }

    /**
     * @param tag to find fragment by tag
     */
    override fun replaceFragment(container: Int, fragment: Fragment, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.replace(container, fragment)
        } else {
            transaction.replace(container, fragment, tag)
        }
        transaction.commitNow()
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