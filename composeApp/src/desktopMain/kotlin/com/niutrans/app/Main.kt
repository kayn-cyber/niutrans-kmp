package com.niutrans.app

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.niutrans.app.storage.createConfigStore
import com.niutrans.app.ui.DesktopControls
import com.niutrans.app.ui.TranslatorApp

fun main() = application {
    val store = remember { createConfigStore() }
    var config by remember { mutableStateOf(store.load()) }
    var isMiniMode by remember { mutableStateOf(false) }

    val windowState = rememberWindowState(
        size = DpSize(460.dp, 460.dp),
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "小牛悬浮翻译",
        undecorated = true,
        transparent = true,
        alwaysOnTop = config.isPinned,
    ) {
        TranslatorApp(
            isDesktop = true,
            configStore = store,
            onConfigChanged = { updated -> config = updated },
            desktopControls = DesktopControls(
                isMiniMode = isMiniMode,
                onTogglePin = {
                    val updated = config.copy(isPinned = !config.isPinned)
                    config = updated
                    store.save(updated)
                },
                onCycleOpacity = {
                    val levels = listOf(1.0f, 0.92f, 0.82f, 0.72f)
                    val next = levels.firstOrNull { config.opacity >= it - 0.03f }
                        ?.let { levels[(levels.indexOf(it) + 1) % levels.size] }
                        ?: levels.first()
                    val updated = config.copy(opacity = next)
                    config = updated
                    store.save(updated)
                },
                onToggleMiniMode = {
                    isMiniMode = !isMiniMode
                    windowState.size = if (isMiniMode) {
                        DpSize(340.dp, 54.dp)
                    } else {
                        DpSize(460.dp, 460.dp)
                    }
                },
                onClose = ::exitApplication,
                headerContainer = { content ->
                    WindowDraggableArea {
                        content()
                    }
                },
            ),
        )
    }
}
