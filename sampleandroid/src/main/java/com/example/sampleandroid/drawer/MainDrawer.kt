package com.example.sampleandroid.drawer

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.sampleandroid.CommonState
import com.example.sampleandroid.R
import com.example.sampleandroid.home.HomeScreen
import com.example.sampleandroid.library.*
import com.example.sampleandroid.util.colors
import com.example.sampleandroid.util.shapes
import com.example.sampleandroid.util.typography

@Composable
fun HomeDrawer() {
    val homeScreen = ScreenStack.find() ?: HomeScreen()

    val closeAndNavigateTo: (tab: TabView) -> Unit = { tab ->
        CommonState.closeDrawer()

        if (!homeScreen.popUpTo(false)) {
            homeScreen.push()
        }
        homeScreen.currentTab = tab
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo(homeScreen.title, Modifier.padding(16.dp))
        Divider(color = colors.onSurface.copy(alpha = .2f))

        homeScreen.tabs.forEach { tab ->
            DrawerButton(
                icon = tab.icon,
                label = tab.title,
                isSelected = homeScreen.currentTab === tab,
                action = { closeAndNavigateTo(tab) }
            )
        }
    }
}

@Composable
private fun Logo(title: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Image(
            asset = vectorResource(R.drawable.ic_android),
            colorFilter = ColorFilter.tint(colors.primary)
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
    val textIconColor = if (isSelected) colors.primary else colors.onSurface.copy(alpha = 0.6f)
    val backgroundColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        color = backgroundColor,
        shape = shapes.small
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
                    style = typography.body2,
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