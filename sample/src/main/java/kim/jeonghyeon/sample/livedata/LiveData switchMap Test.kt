package kim.jeonghyeon.sample.livedata

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import kim.jeonghyeon.androidlibrary.deprecated.BaseLiveData

fun testLiveDataActiveDeactiveRepeat() {
    val start = MutableLiveData<String>()
    val liveData = start.switchMap {
        println("switchMap $it")
        TestLiveData("a")
    }.switchMap {
        println("switchMap $it")
        TestLiveData("b")
    }.switchMap {
        println("switchMap $it")
        TestLiveData("c")
    }
    val observer = object: Observer<String> {
        override fun onChanged(t: String?) {
            println("observe : $t")
        }
    }
    liveData.observeForever(observer)
    val handler = Handler()
    handler.postDelayed({
        //inactive() order : a -> b -> c
        liveData.removeObserver(observer)
        handler.postDelayed({
            //active() order : a -> b -> c
            //check if source is updated or not, if not updated. just return last value
            liveData.observeForever(observer)
        },1000)
    }, 1000)



    start.value = "start"
}

class TestLiveData(val name: String) : BaseLiveData<String>() {
    override fun onFirstActive() {
        super.onFirstActive()
        Handler().postDelayed({
            postValue(name)
        }, 1000)
    }

    override fun onActive() {
        super.onActive()
        println("$name active")
    }

    override fun postValue(value: String?) {
        super.postValue(value)
        println("$name post $value")
    }

    override fun onInactive() {
        println("$name inactive")
        super.onInactive()
    }
}
