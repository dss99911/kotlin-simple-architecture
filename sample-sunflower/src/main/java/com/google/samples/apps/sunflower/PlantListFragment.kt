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

package com.google.samples.apps.sunflower

import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel

class PlantListFragment : BaseFragment() {


    val viewModel: PlantListViewModel by bindingViewModel()
    override val layoutId = R.layout.fragment_plant_list

    //todo check if this approach is fine
    init {
        setMenu(R.menu.menu_plant_list) {item ->
            when (item.itemId) {
                R.id.filter_zone -> {
                    viewModel.onFilterZoneClick()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }
}