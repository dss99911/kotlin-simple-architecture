package com.example.sampleandroid.drawer

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.sampleandroid.CommonState
import com.example.sampleandroid.library.ScreenStack
import com.example.sampleandroid.library.SpacerH
import com.example.sampleandroid.library.SpacerW
import com.example.sampleandroid.library.replace
import com.example.sampleandroid.model.ModelScreen
import com.example.sampleandroid.util.colors
import com.example.sampleandroid.util.shapes
import com.example.sampleandroid.util.typography

@Composable
fun ModelDrawer() {
    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo()
        Divider(color = colors.onSurface.copy(alpha = .2f))


        ModelScreen.screens.forEach { screen ->
            DrawerButton(
                label = screen.title,
                isSelected = ScreenStack.last() === screen,
                action = {
                    CommonState.closeDrawer()
                    screen.replace()
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
                colorFilter = ColorFilter.tint(colors.primary)
            )
            SpacerW(24.dp)
            Text((ScreenStack.last() as ModelScreen).parentTitle)
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
            Text(
                text = label,
                style = typography.body2,
                color = textIconColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview
@Composable
fun PreviewModelDrawer() {
    ModelDrawer()
}