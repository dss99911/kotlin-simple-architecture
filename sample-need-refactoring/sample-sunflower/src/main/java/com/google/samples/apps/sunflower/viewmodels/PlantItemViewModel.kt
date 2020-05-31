package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.ViewModel
import com.google.samples.apps.sunflower.data.Plant
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

class PlantItemViewModel(val plant: Plant, val onClick: () -> Unit) : ViewModel(), DiffComparable<PlantItemViewModel> {
    override fun areItemsTheSame(item: PlantItemViewModel): Boolean =
        plant.plantId == item.plant.plantId

    override fun areContentsTheSame(item: PlantItemViewModel): Boolean =
        plant == item.plant
}