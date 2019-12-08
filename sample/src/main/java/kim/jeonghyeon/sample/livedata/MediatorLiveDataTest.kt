package kim.jeonghyeon.sample.livedata

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kim.jeonghyeon.androidlibrary.extension.handler
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.kotlinlibrary.extension.plus
import kotlin.random.Random


/**
 * when you make data by merging two source
 */
fun test1() {
    val sourceLiveData1 = MutableLiveData<Int>()
    val sourceLiveData2 = MutableLiveData<String>()
    val mediatorLiveData = MediatorLiveData<String>()

    val mediatorAction = Observer<Any> {
        mediatorLiveData.value = sourceLiveData1.value.plus(sourceLiveData2.value)
    }
    mediatorLiveData.addSource(sourceLiveData1, mediatorAction)
    mediatorLiveData.addSource(sourceLiveData2, mediatorAction)


}

/**
 * when the source data is changed, if need action is required
 */
fun test2() {
    val sourceLiveData1 = MutableLiveData<Int>()
    val sourceLiveData2 = MutableLiveData<String>()
    val mediatorLiveData = MediatorLiveData<String>()

    val mediatorAction = Observer<Any> {
        //do action
    }
    mediatorLiveData.addSource(sourceLiveData1, mediatorAction)
    mediatorLiveData.addSource(sourceLiveData2, mediatorAction)
}

fun test3() {
    val sourceLiveData1 = MutableLiveData<Int>()
    val mediatorLiveData = MediatorLiveData<String>()

    mediatorLiveData.addSource(sourceLiveData1) {
        //do check something and update.
    }
}

/**
 * 1. if there is no observer, although you add addSource, source change event doesn't occur
 * 2. if addSource, source's observer doesn't receive event.
 * 3. if there is value set(even though it is null), although addSource is added later, event occur
 */
fun testWhenSourceChangeObserverEventOccur() {
    val mutableLiveData = MutableLiveData<String>()
    val s = "1"
    mutableLiveData.value = s
    mutableLiveData.observeForever {
        //can't receive event, if added as a source
        toast(Random.nextInt().toString())
    }

    val mediatorLiveData = MediatorLiveData<String>()
    mediatorLiveData.addSource(mutableLiveData) {
        toast("mediator add source")//this occur 2times,
        // 1. on adding the source, if there is value, event occur
        // 2. after 2sec, value is set again, so event occur
    }

    mediatorLiveData.observeForever {
        toast("mediator")//doesn't occur, cuz mediator's value is not changed.
    }

    handler.postDelayed({
        mutableLiveData.value = s
    }, 2000)
}


