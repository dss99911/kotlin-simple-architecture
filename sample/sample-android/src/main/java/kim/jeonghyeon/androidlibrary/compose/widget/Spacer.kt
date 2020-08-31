package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpacerW(dp: Dp) = Spacer(Modifier.preferredWidth(dp))

@Composable
fun SpacerH(dp: Dp) = Spacer(Modifier.preferredHeight(dp))

@Composable
fun Spacer(dp: Dp) = Spacer(Modifier.preferredSize(dp))