package com.example.sampleandroid.view.drawer

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.sampleandroid.CommonState
import com.example.sampleandroid.view.SubScreen
import com.example.sampleandroid.view.model.ModelScreen
import kim.jeonghyeon.androidlibrary.compose.ScreenStack
import kim.jeonghyeon.androidlibrary.compose.replace
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerH
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerW

@Composable
fun SubDrawer(screens: List<Pair<String, () -> SubScreen>>) {
    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo()
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))


        screens.forEach {
            DrawerButton(
                label = it.first,
                isSelected = ScreenStack.last().title == it.first,
                action = {
                    CommonState.closeDrawer()
                    it.second().replace()
                }
            )
        }
    }
}

@Composable
private fun Logo() {
    TextButton(
        onClick = {
            CommonState.closeDrawer()
            ScreenStack.pop()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp)) {
            Image(
                asset = Icons.Filled.ArrowBack,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
            )
            SpacerW(24.dp)
            Text((ScreenStack.last() as SubScreen).parentTitle)
        }
    }


}

@Composable
private fun DrawerButton(
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                color = textIconColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview
@Composable
fun PreviewModelDrawer() {
    SubDrawer(ModelScreen.screens)
}