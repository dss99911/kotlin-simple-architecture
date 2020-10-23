package com.example.sampleandroid.view.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerH
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerW
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.Navigator
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ModelViewModel
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

@Composable
fun SubDrawer(viewModels: List<() -> SampleViewModel>, closeDrawer: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        SpacerH(24.dp)
        Logo(closeDrawer)
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))


        viewModels.forEach { vm ->
            val viewModel = vm()

            DrawerButton(
                label = viewModel.title,
                isSelected = (Navigator.current as? SampleViewModel)?.title == viewModel.title,
                action = {
                    closeDrawer()
                    Navigator.replace(viewModel)
                }
            )
        }
    }
}

@Composable
private fun Logo(closeDrawer: () -> Unit) {
    TextButton(
        onClick = {
            closeDrawer()
            Navigator.back()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp)) {
            Image(
                asset = Icons.Filled.ArrowBack,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
            )
            SpacerW(24.dp)
            Text(
                if (Navigator.current is ModelViewModel) {
                    R.string.model.resourceToString()
                } else {
                    R.string.view.resourceToString()
                }
            )
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
    SubDrawer(listOf()) {}
}