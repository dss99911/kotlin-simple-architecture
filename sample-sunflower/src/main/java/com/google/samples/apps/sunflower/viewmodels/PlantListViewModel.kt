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

import com.google.samples.apps.sunflower.HomeViewPagerFragmentDirections
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.map
import kim.jeonghyeon.androidlibrary.architecture.livedata.switchMap
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

/**
 * The ViewModel for [PlantListFragment].
 */
class PlantListViewModel internal constructor(plantRepository: PlantRepository) : BaseViewModel() {

    private val growZoneNumber = LiveObject(NO_GROW_ZONE)

    val plants: LiveObject<List<PlantItemViewModel>> = growZoneNumber.switchMap {
        if (it == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            plantRepository.getPlantsWithGrowZoneNumber(it)
        }
    }.map { convertToViewModel(it) }

    fun onFilterZoneClick() {
        if (isFiltered()) {
            clearGrowZoneNumber()
        } else {
            setGrowZoneNumber(9)
        }
    }

    private fun convertToViewModel(it: List<Plant>): List<PlantItemViewModel> {
        return it.map {
            PlantItemViewModel(it) {
                val direction =
                    HomeViewPagerFragmentDirections.actionViewPagerFragmentToPlantDetailFragment(
                        it.plantId
                    )
                navigateDirection(direction)
            }
        }
    }

    private fun setGrowZoneNumber(num: Int) {
        growZoneNumber.value = num
    }

    private fun clearGrowZoneNumber() {
        growZoneNumber.value = NO_GROW_ZONE
    }
    private fun isFiltered() = growZoneNumber.value != NO_GROW_ZONE

    companion object {
        private const val NO_GROW_ZONE = -1
    }
}
