package com.johan.misgastos.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class SnackbarController(
    private val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun showMessage(message: String) {
        scope.launch {
            hostState.showSnackbar(message)
        }
    }
}

val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("SnackbarController no disponible")
}
