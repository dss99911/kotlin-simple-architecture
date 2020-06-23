package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.unit.Dp

@Composable
fun SpacerW(dp: Dp) = Spacer(Modifier.preferredWidth(dp))

@Composable
fun SpacerH(dp: Dp) = Spacer(Modifier.preferredHeight(dp))

@Composable
fun Spacer(dp: Dp) = Spacer(Modifier.preferredSize(dp))