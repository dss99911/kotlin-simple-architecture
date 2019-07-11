package kim.jeonghyeon.androidlibrary.sample.livedata

import androidx.lifecycle.MutableLiveData
import kim.jeonghyeon.androidlibrary.extension.handler
import kim.jeonghyeon.androidlibrary.extension.toast
import kotlin.random.Random

class LiveDataWhenObserverEventOccur {
    fun testWhenObserverReceive() {
        val mutableLiveData = MutableLiveData<String>()
        mutableLiveData.value = null
        handler.postDelayed({
            mutableLiveData.observeForever {
                //at first time, if there is value, receive event. even though it is null, receive event
                toast(Random.nextInt().toString())

            }
        }, 2000)
    }

    fun testWhenObserverReceive2() {
        val mutableLiveData = MutableLiveData<String>()
//        mutableLiveData.value = "1"
        handler.postDelayed({
            mutableLiveData.observeForever {
                //if there is no value set, then doesn't receive event
                toast(Random.nextInt().toString())

            }
        }, 2000)
    }

    fun testWhenObserverReceive3() {
        val mutableLiveData = MutableLiveData<String>()
        val s = "1"
        mutableLiveData.value = s
        mutableLiveData.observeForever {
            toast(Random.nextInt().toString())

        }

        handler.postDelayed({
            mutableLiveData.value = s//even though, same value is input, event happen.
            //although same data is set, event occur
        }, 2000)
    }
}