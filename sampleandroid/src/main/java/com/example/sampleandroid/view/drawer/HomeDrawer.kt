package com.example.sampleandroid.view.drawer

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.sampleandroid.CommonState
import com.example.sampleandroid.R
import com.example.sampleandroid.view.home.HomeScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.ScreenStack
import kim.jeonghyeon.androidlibrary.compose.popUpTo
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerH
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerW

@Composable
fun HomeDrawer() {
    val homeScreen = ScreenStack.find() ?: HomeScreen()

    val closeAndNavigateTo: (tab: Screen) -> Unit = { tab ->
        CommonState.closeDrawer()

        if (!homeScreen.popUpTo(false)) {
            homeScreen.push()
        }
        homeScreen.currentTab = tab
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo(homeScreen.title, Modifier.padding(16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))

        homeScreen.tabs.forEach { tab ->
            DrawerButton(
                icon = tab.first,
                label = tab.second.title,
                isSelected = homeScreen.currentTab === tab,
                action = { closeAndNavigateTo(tab.second) }
            )
        }
    }
}

@Composable
private fun Logo(title: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Image(
            asset = vectorResource(R.drawable.ic_android),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )
        SpacerW(16.dp)
        Text(title)

    }

}

@Composable
private fun DrawerButton(
    icon: VectorAsset?,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageAlpha = if (isSelected) 1f else 0.6f
    val textIconColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    val backgroundColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.12f) else MaterialTheme.colors.surface

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalGravity = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    Image(
                        asset = icon,
                        colorFilter = ColorFilter.tint(textIconColor),
                        alpha = imageAlpha
                    )
                    SpacerW(16.dp)
                }

                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewMainDrawer() {
    HomeDrawer()
}