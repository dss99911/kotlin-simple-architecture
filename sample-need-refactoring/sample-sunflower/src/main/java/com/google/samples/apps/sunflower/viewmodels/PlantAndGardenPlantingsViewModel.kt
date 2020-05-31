/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable
import java.text.SimpleDateFormat
import java.util.*

/**
 * this is not related to lifecycle. so, using livedata is not the proper option.
 * just use variable. and it will be updated when binding data only. so if the data should be refreshed to UI not on the binding time.
 * you have to use mBinding.executePendingBindings();
 */
class PlantAndGardenPlantingsViewModel(
    plantings: PlantAndGardenPlantings,
    val onClick: (String) -> Unit
) : DiffComparable<PlantAndGardenPlantingsViewModel> {
    val plant = checkNotNull(plantings.plant)
    val gardenPlanting = plantings.gardenPlantings[0]

    val waterDateString: String = dateFormat.format(gardenPlanting.lastWateringDate.time)
    val wateringInterval
        get() = plant.wateringInterval
    val imageUrl
        get() = plant.imageUrl
    val plantName
        get() = plant.name
    val plantDateString: String = dateFormat.format(gardenPlanting.plantDate.time)
    val plantId
        get() = plant.plantId


    companion object {
        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    }

    override fun areItemsTheSame(item: PlantAndGardenPlantingsViewModel): Boolean =
        plant.plantId == item.plant.plantId

    override fun areContentsTheSame(item: PlantAndGardenPlantingsViewModel): Boolean =
        plant == item.plant
}