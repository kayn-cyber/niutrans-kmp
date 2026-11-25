package com.niutrans.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.niutrans.app.storage.AndroidContextHolder
import com.niutrans.app.ui.TranslatorApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContextHolder.appContext = applicationContext
        enableEdgeToEdge()
        setContent {
            TranslatorApp(isDesktop = false)
        }
    }
}
