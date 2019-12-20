package kim.jeonghyeon.androidlibrary.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kim.jeonghyeon.androidlibrary.extension.log

interface IBaseActivity {
    fun addFragment(container: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null)
}

open class BaseActivity : AppCompatActivity(), IBaseActivity {

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
}