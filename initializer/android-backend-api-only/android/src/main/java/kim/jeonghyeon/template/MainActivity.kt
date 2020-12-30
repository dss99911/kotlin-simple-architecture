package kim.jeonghyeon.template

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import kim.jeonghyeon.base.HomeViewModel
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity() {
    lateinit var textView: TextView
    val viewModel: HomeViewModel by lazy { HomeViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textView = TextView(this)
        setContentView(textView)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        GlobalScope.launch {
            val message = try {
                viewModel.getData().value
            } catch (e: Exception) {
                e.printStackTrace()
                e.message
            }
            textView.text = message
        }
    }

}