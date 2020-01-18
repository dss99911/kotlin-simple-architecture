package com.google.samples.apps.sunflower.viewmodels

import kim.jeonghyeon.androidlibrary.architecture.livedata.BaseLiveData
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class GardenViewModel : BaseViewModel() {
    val currentItem = BaseLiveData<Int>()
}