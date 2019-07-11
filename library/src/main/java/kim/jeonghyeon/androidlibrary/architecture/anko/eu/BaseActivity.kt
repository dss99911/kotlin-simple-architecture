//package kim.jeonghyeon.androidlibrary.architecture.anko.eu
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//
//abstract class BaseActivity<out UI : BaseUI, EVENT : BaseEvent> : AppCompatActivity() {
//
//    private lateinit var component: BaseComponent<EVENT, UI>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        component = createComponent()
//        component.setContentView(this)
//    }
//
//    val ui: UI
//        get() = component.ui
//
//    abstract fun getEvent(): EVENT
//
//    abstract fun createComponent(): BaseComponent<EVENT, UI>
//}