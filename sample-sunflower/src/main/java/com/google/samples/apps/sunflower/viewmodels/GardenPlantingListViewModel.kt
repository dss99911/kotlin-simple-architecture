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

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.samples.apps.sunflower.adapters.PLANT_LIST_PAGE_INDEX
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.LiveEvent
import kim.jeonghyeon.androidlibrary.architecture.mvvm.addEventSource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.call

class GardenPlantingListViewModel internal constructor(
    val parent: GardenViewModel,
    gardenPlantingRepository: GardenPlantingRepository
) : BaseViewModel() {
    val plantAndGardenPlantings: LiveData<List<PlantAndGardenPlantings>> = gardenPlantingRepository.getPlantedGardens()

    val clickEvent = LiveEvent<String>()
    val addClickEvent = LiveEvent<Unit>()

    val plantAndGardengPlantingsViewModel: LiveData<List<PlantAndGardenPlantingsViewModel>> =
        plantAndGardenPlantings.map { list ->
            list.map {
                PlantAndGardenPlantingsViewModel(it) {
                    clickEvent.call(it)
                }
            }
        }

    init {
        parent.currentItem.addEventSource(addClickEvent) {
            parent.currentItem.value = PLANT_LIST_PAGE_INDEX
        }
    }
}