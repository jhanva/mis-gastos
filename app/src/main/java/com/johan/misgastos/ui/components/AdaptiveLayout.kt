package com.johan.misgastos.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppWidthSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

@Composable
fun rememberAppWidthSizeClass(): AppWidthSizeClass {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return when {
        screenWidthDp >= 840 -> AppWidthSizeClass.EXPANDED
        screenWidthDp >= 600 -> AppWidthSizeClass.MEDIUM
        else -> AppWidthSizeClass.COMPACT
    }
}

fun contentHorizontalPadding(widthSizeClass: AppWidthSizeClass): Dp {
    return when (widthSizeClass) {
        AppWidthSizeClass.COMPACT -> 20.dp
        AppWidthSizeClass.MEDIUM -> 24.dp
        AppWidthSizeClass.EXPANDED -> 32.dp
    }
}

@Composable
fun ResponsiveContentFrame(
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = 960.dp,
    content: @Composable BoxScope.(AppWidthSizeClass) -> Unit,
) {
    val widthSizeClass = rememberAppWidthSizeClass()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxContentWidth),
        ) {
            content(widthSizeClass)
        }
    }
}
