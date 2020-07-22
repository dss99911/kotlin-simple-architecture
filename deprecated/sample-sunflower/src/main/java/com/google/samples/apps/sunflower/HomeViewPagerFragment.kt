/*
 * Copyright 2019 Google LLC
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

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.google.samples.apps.sunflower.adapters.MY_GARDEN_PAGE_INDEX
import com.google.samples.apps.sunflower.adapters.PLANT_LIST_PAGE_INDEX
import com.google.samples.apps.sunflower.adapters.SunflowerPagerAdapter
import com.google.samples.apps.sunflower.viewmodels.GardenViewModel
import com.google.samples.apps.sunflower.viewmodels.HomeViewPagerViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kotlinx.android.synthetic.main.fragment_view_pager.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.core.parameter.parametersOf

class HomeViewPagerFragment : BaseFragment() {

    val viewModel: HomeViewPagerViewModel by bindingViewModel {
        parametersOf(getSharedViewModel<GardenViewModel>())
    }

    override val layoutId = R.layout.fragment_view_pager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view_pager.adapter = SunflowerPagerAdapter(this)
        TabLayoutMediator(tabs, view_pager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            MY_GARDEN_PAGE_INDEX -> R.drawable.garden_tab_selector
            PLANT_LIST_PAGE_INDEX -> R.drawable.plant_list_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            MY_GARDEN_PAGE_INDEX -> getString(R.string.my_garden_title)
            PLANT_LIST_PAGE_INDEX -> getString(R.string.plant_list_title)
            else -> null
        }
    }
}