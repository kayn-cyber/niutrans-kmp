package com.niutrans.app

import androidx.compose.ui.window.ComposeUIViewController
import com.niutrans.app.ui.TranslatorApp

fun MainViewController() = ComposeUIViewController {
    TranslatorApp(isDesktop = false)
}
