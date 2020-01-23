package com.google.samples.apps.sunflower.viewmodels

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class GardenViewModel : BaseViewModel() {
    val currentItem = LiveObject<Int>()
}