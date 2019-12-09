package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.MediatorLiveData
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class GardenViewModel : BaseViewModel() {
    val currentItem = MediatorLiveData<Int>()
}