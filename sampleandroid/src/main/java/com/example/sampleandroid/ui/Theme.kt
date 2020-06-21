package com.example.sampleandroid.ui

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import androidx.ui.unit.sp

@Composable
fun AndroidLibraryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

private val darkColorPalette = darkColorPalette(
    primary = purple200,
    primaryVariant = purple700,
    secondary = teal200
)

private val lightColorPalette = lightColorPalette(
    primary = purple500,
    primaryVariant = purple700,
    secondary = teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)


// Set of Material typography styles to start with
private val typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    children: @Composable() () -> Unit
) {
    AndroidLibraryTheme(darkTheme = darkTheme) {
        Surface {
            children()
        }
    }
}
