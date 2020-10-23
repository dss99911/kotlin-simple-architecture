package com.example.sampleandroid.view.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.sampleandroid.view.home.homeTabList
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerH
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerW
import kim.jeonghyeon.client.Navigator
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.HomeViewModel

@Composable
fun HomeDrawer(closeDrawer: () -> Unit) {
    val homeViewModel = Navigator.backStack.lastOrNull {
        it is HomeViewModel
    } as? HomeViewModel ?: HomeViewModel()

    val closeAndNavigateTo: (index: Int) -> Unit = { tabIndex ->
        closeDrawer()

        if (!Navigator.backUpTo(homeViewModel)) {
            Navigator.navigate(homeViewModel)
        }
        homeViewModel.currentTabIndex.setValue(tabIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo(homeViewModel.title, Modifier.padding(16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))

        homeTabList.forEachIndexed { index, tab ->
            DrawerButton(
                icon = tab.icon,
                label = tab.title,
                isSelected = +homeViewModel.currentTabIndex == index,
                action = { closeAndNavigateTo(index) }
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
    val textIconColor =
        if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    val backgroundColor =
        if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.12f) else MaterialTheme.colors.surface

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
    HomeDrawer {}
}