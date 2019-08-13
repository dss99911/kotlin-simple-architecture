package kim.jeonghyeon.sample

import android.os.Bundle
import kim.jeonghyeon.androidlibrary.architecture.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(R.id.layout_container, MainFragment())
    }


}
