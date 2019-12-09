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

import android.view.MenuItem
import com.google.samples.apps.sunflower.PlantDetailFragment
import com.google.samples.apps.sunflower.PlantDetailFragmentArgs
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantRepository
import kim.jeonghyeon.androidlibrary.architecture.coroutine.launch
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MutableLiveEvent
import kim.jeonghyeon.androidlibrary.architecture.mvvm.call

/**
 * The ViewModel used in [PlantDetailFragment].
 */
class PlantDetailViewModel(
    plantRepository: PlantRepository,
    private val gardenPlantingRepository: GardenPlantingRepository,
    private val navArgs: PlantDetailFragmentArgs
) : BaseViewModel() {

    val isPlanted = gardenPlantingRepository.isPlanted(navArgs.plantId)
    val plant = plantRepository.getPlant(navArgs.plantId)

    val fabHideEvent = MutableLiveEvent<Unit>()
    val startShareEvent = MutableLiveEvent<Unit>()

    fun onFabClick() {
        fabHideEvent.call()
        addPlantToGarden()
        showSnackbar(R.string.added_plant_to_garden)
    }

    fun onMenuClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_share -> {
            startShareEvent.call()
            true
        }
        else -> false
    }

    fun onNavigationClick() {
        navigate {
            it.navigateUp()
        }
    }

    fun addPlantToGarden() {
        launch {
            gardenPlantingRepository.createGardenPlanting(navArgs.plantId)
        }
    }
}
