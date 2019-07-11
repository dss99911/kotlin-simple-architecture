//package kim.jeonghyeon.androidlibrary.architecture.anko.eu
//
//import android.view.View
//import org.jetbrains.anko.AnkoComponent
//import org.jetbrains.anko.AnkoContextImpl
//
//abstract class BaseComponent<in E : BaseEvent, out U : BaseUI> : AnkoComponent<E> {
//    abstract val ui: U
//}
//
//fun <E : BaseEvent, U : BaseUI> BaseComponent<E, U>.setContentView(activity: BaseActivity<U, E>): View =
//        createView(AnkoContextImpl(activity, activity.getEvent(), true))