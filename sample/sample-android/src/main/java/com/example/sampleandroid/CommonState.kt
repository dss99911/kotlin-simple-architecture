package com.example.sampleandroid

import androidx.ui.material.DrawerState
import androidx.ui.material.ScaffoldState

object CommonState {
    val scaffoldState = ScaffoldState()

    fun closeDrawer() {
        scaffoldState.drawerState = DrawerState.Closed
    }

    fun openDrawer() {
        scaffoldState.drawerState = DrawerState.Opened
    }
}